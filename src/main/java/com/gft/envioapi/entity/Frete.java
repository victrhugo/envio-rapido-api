package com.gft.envioapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "frete")
public class Frete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long freteId;

    public Frete(Long freteId, Envio envio, String pacValor, String pacPrazo, Boolean pacDisponivel, String pacMensagem, String sedexValor, String sedexPrazo, Boolean sedexDisponivel, String sedexMensagem, String mensagemGeral) {
        this.freteId = freteId;
        this.envio = envio;
        this.pacValor = pacValor;
        this.pacPrazo = pacPrazo;
        this.pacDisponivel = pacDisponivel;
        this.pacMensagem = pacMensagem;
        this.sedexValor = sedexValor;
        this.sedexPrazo = sedexPrazo;
        this.sedexDisponivel = sedexDisponivel;
        this.sedexMensagem = sedexMensagem;
        this.mensagemGeral = mensagemGeral;
    }

    public Frete() {}

    @OneToOne
    @JoinColumn(name = "envio_id", nullable = false, unique = true)
    private Envio envio;

    private String pacValor;
    private String pacPrazo;
    private Boolean pacDisponivel;
    private String pacMensagem;

    private String sedexValor;
    private String sedexPrazo;
    private Boolean sedexDisponivel;
    private String sedexMensagem;

    public Long getFreteId() {
        return freteId;
    }

    public void setFreteId(Long freteId) {
        this.freteId = freteId;
    }

    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
    }

    public String getPacValor() {
        return pacValor;
    }

    public void setPacValor(String pacValor) {
        this.pacValor = pacValor;
    }

    public String getPacPrazo() {
        return pacPrazo;
    }

    public void setPacPrazo(String pacPrazo) {
        this.pacPrazo = pacPrazo;
    }

    public Boolean getPacDisponivel() {
        return pacDisponivel;
    }

    public void setPacDisponivel(Boolean pacDisponivel) {
        this.pacDisponivel = pacDisponivel;
    }

    public String getPacMensagem() {
        return pacMensagem;
    }

    public void setPacMensagem(String pacMensagem) {
        this.pacMensagem = pacMensagem;
    }

    public String getSedexValor() {
        return sedexValor;
    }

    public void setSedexValor(String sedexValor) {
        this.sedexValor = sedexValor;
    }

    public String getSedexPrazo() {
        return sedexPrazo;
    }

    public void setSedexPrazo(String sedexPrazo) {
        this.sedexPrazo = sedexPrazo;
    }

    public Boolean getSedexDisponivel() {
        return sedexDisponivel;
    }

    public void setSedexDisponivel(Boolean sedexDisponivel) {
        this.sedexDisponivel = sedexDisponivel;
    }

    public String getMensagemGeral() {
        return mensagemGeral;
    }

    public void setMensagemGeral(String mensagemGeral) {
        this.mensagemGeral = mensagemGeral;
    }

    public String getSedexMensagem() {
        return sedexMensagem;
    }

    public void setSedexMensagem(String sedexMensagem) {
        this.sedexMensagem = sedexMensagem;
    }

    private String mensagemGeral;
}