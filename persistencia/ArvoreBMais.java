package persistencia;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Árvore B+ com nós armazenados em arquivo binário.
 *
 * Estrutura do arquivo (.db):
 *   Cabeçalho (16 bytes):
 *     [0]  long  - endereço da raiz (-1 se vazia)
 *     [8]  int   - ordem (número máximo de chaves por nó)
 *     [12] int   - reservado
 *
 * Cada nó ocupa um bloco de tamanho fixo = TAM_NO bytes:
 *     byte  - tipo: 0 = interno, 1 = folha
 *     int   - qtdChaves (número de chaves presentes)
 *     long  - próximaFolha (somente folhas; -1 se nenhuma)
 *     [ordem]   chaves (String UTF, padded)
 *     [ordem+1] filhos/endereços (long)
 *       - nó interno: filho[i] aponta para nó filho
 *       - nó folha:   filho[i] aponta para endereço do registro no arquivo de dados
 *
 * A chave usada externamente é "id|titulo_lower", garantindo ordenação
 * lexicográfica correta por título e desempate por ID.
 *
 * Escolha da B+:
 *   - Consultas ordenadas sem ordenação em memória: percorre-se a lista
 *     encadeada de nós folha da esquerda para a direita em O(n).
 *   - Inserção/remoção em O(log n) com acesso a disco localizado.
 *   - Hash Extensível (usado nos demais índices) não suporta varredura
 *     ordenada; por isso a B+ é empregada apenas onde a ordem importa.
 */
public class ArvoreBMais {

    // ── Constantes de layout ──────────────────────────────────────────────
    private static final int ORDEM          = 5;   // máx de chaves por nó
    private static final int TAM_CHAVE      = 120; // bytes reservados por chave (UTF padded)
    private static final int TAM_CABECALHO  = 16;

    // Tamanho de um nó:
    //   1 (tipo) + 4 (qtd) + 8 (próxFolha) + ORDEM*TAM_CHAVE + (ORDEM+1)*8
    private static final int TAM_NO =
            1 + 4 + 8 + ORDEM * TAM_CHAVE + (ORDEM + 1) * 8;

    private static final byte FOLHA   = 1;
    private static final byte INTERNO = 0;
    private static final long NULO    = -1L;

    // ── Estado ────────────────────────────────────────────────────────────
    private final RandomAccessFile arq;
    private long raiz;

    // ── Construtor ───────────────────────────────────────────────────────
    public ArvoreBMais(String dirBase, String nomeIndice) throws Exception {
        File dir = new File("./dados/" + dirBase);
        if (!dir.exists()) dir.mkdirs();

        arq = new RandomAccessFile("./dados/" + dirBase + "/" + nomeIndice + ".db", "rw");

        if (arq.length() < TAM_CABECALHO) {
            // Arquivo novo: grava cabeçalho
            arq.seek(0);
            arq.writeLong(NULO);   // raiz inexistente
            arq.writeInt(ORDEM);
            arq.writeInt(0);
            raiz = NULO;
        } else {
            arq.seek(0);
            raiz = arq.readLong();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // API pública
    // ══════════════════════════════════════════════════════════════════════

    /** Insere ou atualiza a chave com o endereço do registro. */
    public void put(String chave, long endereco) throws Exception {
        if (raiz == NULO) {
            raiz = novoNo(FOLHA);
            gravarRaiz();
        }
        ResultadoInsercao res = inserir(raiz, chave, endereco);
        if (res != null) {
            // A raiz foi dividida — cria nova raiz interna
            long novaRaiz = novoNo(INTERNO);
            No r = lerNo(novaRaiz);
            r.qtdChaves = 1;
            r.chaves[0] = res.chavePromovida;
            r.filhos[0] = raiz;
            r.filhos[1] = res.novoNo;
            gravarNo(novaRaiz, r);
            raiz = novaRaiz;
            gravarRaiz();
        }
    }

    /** Remove a chave do índice. */
    public void remove(String chave) throws Exception {
        if (raiz == NULO) return;
        remover(raiz, chave);
    }

    /**
     * Retorna todos os endereços de registros em ordem crescente de chave,
     * percorrendo a lista encadeada de nós folha — sem ordenação em memória.
     */
    public ArrayList<Long> listAllInOrder() throws Exception {
        ArrayList<Long> lista = new ArrayList<>();
        if (raiz == NULO) return lista;

        // Desce até a folha mais à esquerda
        long posNo = raiz;
        while (true) {
            No no = lerNo(posNo);
            if (no.tipo == FOLHA) break;
            posNo = no.filhos[0];
        }

        // Percorre todas as folhas encadeadas
        while (posNo != NULO) {
            No folha = lerNo(posNo);
            for (int i = 0; i < folha.qtdChaves; i++) {
                lista.add(folha.filhos[i]);
            }
            posNo = folha.proxFolha;
        }
        return lista;
    }

    public void close() throws Exception {
        arq.close();
    }

    // ══════════════════════════════════════════════════════════════════════
    // Estrutura interna: No
    // ══════════════════════════════════════════════════════════════════════

    private static class No {
        byte   tipo;
        int    qtdChaves;
        long   proxFolha;
        String[] chaves;
        long[]   filhos;

        No() {
            chaves  = new String[ORDEM + 1]; // +1 para overflow temporário
            filhos  = new long[ORDEM + 2];
            proxFolha = NULO;
        }
    }

    private static class ResultadoInsercao {
        String chavePromovida;
        long   novoNo;
        ResultadoInsercao(String c, long n) { chavePromovida = c; novoNo = n; }
    }

    // ══════════════════════════════════════════════════════════════════════
    // Inserção recursiva
    // ══════════════════════════════════════════════════════════════════════

    private ResultadoInsercao inserir(long posNo, String chave, long endereco) throws Exception {
        No no = lerNo(posNo);

        if (no.tipo == FOLHA) {
            // Encontra posição de inserção (mantém ordem)
            int pos = 0;
            while (pos < no.qtdChaves && chave.compareTo(no.chaves[pos]) > 0) pos++;

            // Atualiza se chave já existe
            if (pos < no.qtdChaves && chave.equals(no.chaves[pos])) {
                no.filhos[pos] = endereco;
                gravarNo(posNo, no);
                return null;
            }

            // Insere mantendo ordem
            for (int i = no.qtdChaves; i > pos; i--) {
                no.chaves[i]  = no.chaves[i - 1];
                no.filhos[i]  = no.filhos[i - 1];
            }
            no.chaves[pos]  = chave;
            no.filhos[pos]  = endereco;
            no.qtdChaves++;

            if (no.qtdChaves <= ORDEM) {
                gravarNo(posNo, no);
                return null;
            }
            return dividirFolha(posNo, no);

        } else {
            // Nó interno: desce para o filho correto
            int pos = 0;
            while (pos < no.qtdChaves && chave.compareTo(no.chaves[pos]) >= 0) pos++;

            ResultadoInsercao res = inserir(no.filhos[pos], chave, endereco);
            if (res == null) return null;

            // Incorpora chave promovida
            for (int i = no.qtdChaves; i > pos; i--) {
                no.chaves[i]      = no.chaves[i - 1];
                no.filhos[i + 1]  = no.filhos[i];
            }
            no.chaves[pos]      = res.chavePromovida;
            no.filhos[pos + 1]  = res.novoNo;
            no.qtdChaves++;

            if (no.qtdChaves <= ORDEM) {
                gravarNo(posNo, no);
                return null;
            }
            return dividirInterno(posNo, no);
        }
    }

    // ── Divisão de folha ─────────────────────────────────────────────────
    private ResultadoInsercao dividirFolha(long posNo, No no) throws Exception {
        int meio = (ORDEM + 1) / 2;

        long posNovo = novoNo(FOLHA);
        No novo = lerNo(posNovo);

        novo.qtdChaves  = no.qtdChaves - meio;
        novo.proxFolha  = no.proxFolha;

        for (int i = 0; i < novo.qtdChaves; i++) {
            novo.chaves[i]  = no.chaves[meio + i];
            novo.filhos[i]  = no.filhos[meio + i];
        }

        no.qtdChaves  = meio;
        no.proxFolha  = posNovo;

        // Limpa posições que ficaram na direita do nó esquerdo
        for (int i = meio; i <= ORDEM; i++) {
            no.chaves[i]  = null;
            no.filhos[i]  = NULO;
        }

        gravarNo(posNo,  no);
        gravarNo(posNovo, novo);

        return new ResultadoInsercao(novo.chaves[0], posNovo);
    }

    // ── Divisão de nó interno ────────────────────────────────────────────
    private ResultadoInsercao dividirInterno(long posNo, No no) throws Exception {
        int meio = ORDEM / 2;
        String chavePromovida = no.chaves[meio];

        long posNovo = novoNo(INTERNO);
        No novo = lerNo(posNovo);

        novo.qtdChaves = no.qtdChaves - meio - 1;
        for (int i = 0; i < novo.qtdChaves; i++) {
            novo.chaves[i]      = no.chaves[meio + 1 + i];
            novo.filhos[i]      = no.filhos[meio + 1 + i];
        }
        novo.filhos[novo.qtdChaves] = no.filhos[no.qtdChaves];

        no.qtdChaves = meio;
        for (int i = meio; i <= ORDEM; i++) {
            no.chaves[i]  = null;
            no.filhos[i + 1] = NULO;
        }

        gravarNo(posNo,  no);
        gravarNo(posNovo, novo);

        return new ResultadoInsercao(chavePromovida, posNovo);
    }

    // ══════════════════════════════════════════════════════════════════════
    // Remoção (marca chave como nula na folha; sem rebalanceamento completo
    // para simplificar, mas a varredura ordenada permanece correta)
    // ══════════════════════════════════════════════════════════════════════

    private void remover(long posNo, String chave) throws Exception {
        No no = lerNo(posNo);

        if (no.tipo == FOLHA) {
            for (int i = 0; i < no.qtdChaves; i++) {
                if (chave.equals(no.chaves[i])) {
                    // Desloca entradas para a esquerda
                    for (int j = i; j < no.qtdChaves - 1; j++) {
                        no.chaves[j]  = no.chaves[j + 1];
                        no.filhos[j]  = no.filhos[j + 1];
                    }
                    no.chaves[no.qtdChaves - 1]  = null;
                    no.filhos[no.qtdChaves - 1]  = NULO;
                    no.qtdChaves--;
                    gravarNo(posNo, no);
                    return;
                }
            }
        } else {
            int pos = 0;
            while (pos < no.qtdChaves && chave.compareTo(no.chaves[pos]) >= 0) pos++;
            remover(no.filhos[pos], chave);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // I/O de nós (serialização de tamanho fixo)
    // ══════════════════════════════════════════════════════════════════════

    /** Aloca bloco para novo nó no fim do arquivo e retorna sua posição. */
    private long novoNo(byte tipo) throws Exception {
        long pos = Math.max(arq.length(), TAM_CABECALHO);
        // Garante alinhamento ao tamanho do bloco
        if (pos > TAM_CABECALHO) {
            long offset = (pos - TAM_CABECALHO) % TAM_NO;
            if (offset != 0) pos += (TAM_NO - offset);
        }
        No no = new No();
        no.tipo       = tipo;
        no.qtdChaves  = 0;
        no.proxFolha  = NULO;
        for (int i = 0; i < ORDEM + 1; i++) no.filhos[i] = NULO;
        gravarNo(pos, no);
        return pos;
    }

    private No lerNo(long pos) throws Exception {
        arq.seek(pos);
        No no = new No();
        no.tipo      = arq.readByte();
        no.qtdChaves = arq.readInt();
        no.proxFolha = arq.readLong();

        for (int i = 0; i < ORDEM; i++) {
            no.chaves[i] = lerChavePadded();
        }
        for (int i = 0; i <= ORDEM; i++) {
            no.filhos[i] = arq.readLong();
        }
        return no;
    }

    private void gravarNo(long pos, No no) throws Exception {
        arq.seek(pos);
        arq.writeByte(no.tipo);
        arq.writeInt(no.qtdChaves);
        arq.writeLong(no.proxFolha);

        for (int i = 0; i < ORDEM; i++) {
            gravarChavePadded(no.chaves[i]);
        }
        for (int i = 0; i <= ORDEM; i++) {
            arq.writeLong(no.filhos[i] == 0 && i >= no.qtdChaves + 1 ? NULO : no.filhos[i]);
        }
    }

    /** Lê uma String com padding fixo de TAM_CHAVE bytes. */
    private String lerChavePadded() throws Exception {
        byte[] buf = new byte[TAM_CHAVE];
        arq.readFully(buf);
        // Comprimento real está nos 2 primeiros bytes (formato writeShort)
        int len = ((buf[0] & 0xFF) << 8) | (buf[1] & 0xFF);
        if (len == 0xFFFF) return null;
        return new String(buf, 2, len, "UTF-8");
    }

    /** Grava uma String com padding fixo de TAM_CHAVE bytes. */
    private void gravarChavePadded(String s) throws Exception {
        byte[] buf = new byte[TAM_CHAVE];
        if (s == null) {
            buf[0] = (byte) 0xFF;
            buf[1] = (byte) 0xFF;
        } else {
            byte[] encoded = s.getBytes("UTF-8");
            int len = Math.min(encoded.length, TAM_CHAVE - 2);
            buf[0] = (byte) ((len >> 8) & 0xFF);
            buf[1] = (byte) (len & 0xFF);
            System.arraycopy(encoded, 0, buf, 2, len);
        }
        arq.write(buf);
    }

    private void gravarRaiz() throws Exception {
        arq.seek(0);
        arq.writeLong(raiz);
    }
}
