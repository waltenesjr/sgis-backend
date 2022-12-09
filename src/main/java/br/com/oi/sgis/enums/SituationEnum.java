package br.com.oi.sgis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum SituationEnum {

    BXS("BXS", "BAIXA POR SWAP"),
    BXP("BXP", "BAIXA P/ INSTALACAO NO CLIENTE"),
    BXO("BXO", "BAIXA POR OBSELESCENCIA"),
    BXC("BXC", "BAIXA P/ ERRO NO CADASTRO"),
    BXI("BXI", "INSTALADO NA PLANTA"),
    BXU("BXU", "BAIXA POR FIM DE VIDA UTIL"),
    BXE("BXE", "BAIXA POR DANIFICACAO/PERDA"),
    DEF("DEF", "DEFEITUOSO"),
    DIS("DIS", "DISPONIVEL"),
    DVR("DVR", "DEVOLUCAO DE REPARO"),
    EMP("EMP", "EMPRESTADO"),
    EMU("EMU", "EMPRESTADO EM USO"),
    OFE("OFE", "OFERTA"),
    PRE("PRE", "COM O PRESTADOR DE SERVICO"),
    PMO("PMO", "PENDENTE MIGRACAO NA ORIGEM"),
    TDD("TDD", "TRANSITO DE DEFEITUOSO"),
    TDR("TDR", "TRANSITO DEVOLUCAO DE REPARO"),
    TRD("TRD", "TRANSITO DE DEVOLUCAO EMPREST"),
    TRE("TRE", "TRANSFERIDO PARA EMPRESTIMO"),
    TRC("TRC", "TRANSFERIDO PARA CONTÁBIL"),
    TRG("TRG", "TRANSFERIDO PARA OUTRA REGIAO"),
    TRL("TRL", "TRANSFERIDO PARA LOGÍSTICA"),
    TRN("TRN", "TRANSFERIDO NOVO"),
    TRP("TRP", "TRANSFERIDO PROPRIEDADE"),
    TRR("TRR", "TRANSFERIDO PARA REPARO"),
    REP("REP", "EM REPARO"),
    RES("RES", "RESERVA"),
    USO("USO", "EM USO");

    private final String cod;
    private final String description;

    public static List<SituationEnum> canOpenRepair(){
        return List.of(DIS, EMP, USO, EMU, PRE, DVR, DEF);
    }

    public static List<SituationEnum> onRepair(){
        return List.of(TRR, REP, DVR, TDR);
    }
}
