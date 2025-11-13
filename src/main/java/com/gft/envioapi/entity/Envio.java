package com.gft.envioapi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "envios")
public class Envio {

    @OneToOne(mappedBy = "envio", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Frete frete;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long envioId;

    @Column(nullable = false)
    private String nomeRemetente;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String cepOrigem;

    @Column(nullable = false)
    private String cepDestino;

    @Column(nullable = false)
    private double larguraCaixa;

    @Column(nullable = false)
    private double alturaCaixa;

    @Column(nullable = false)
    private double ComprimentoCaixa;

    @Column(nullable = false)
    private double peso;

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    //region construtores
    public Envio() {}

    public Envio(Long envioId, String nomeRemetente, String endereco, String cepOrigem, String cepDestino, double larguraCaixa, double alturaCaixa, double comprimentoCaixa, double peso) {
        this.envioId = envioId;
        this.nomeRemetente = nomeRemetente;
        this.endereco = endereco;
        this.cepOrigem = cepOrigem;
        this.cepDestino = cepDestino;
        this.larguraCaixa = larguraCaixa;
        this.alturaCaixa = alturaCaixa;
        ComprimentoCaixa = comprimentoCaixa;
        this.peso = peso;
    }

    //endregion

    //region getters e setters
    public Long getEnvioId() {
        return envioId;
    }

    public void setEnvioId(Long envioId) {
        this.envioId = envioId;
    }

    public String getNomeRemetente() {
        return nomeRemetente;
    }

    public void setNomeRemetente(String nomeRemetente) {
        this.nomeRemetente = nomeRemetente;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCepOrigem() {
        return cepOrigem;
    }

    public void setCepOrigem(String cepOrigem) {
        this.cepOrigem = cepOrigem;
    }

    public String getCepDestino() {
        return cepDestino;
    }

    public void setCepDestino(String cepDestino) {
        this.cepDestino = cepDestino;
    }

    public double getLarguraCaixa() {
        return larguraCaixa;
    }

    public void setLarguraCaixa(int larguraCaixa) {
        this.larguraCaixa = larguraCaixa;
    }

    public double getAlturaCaixa() {
        return alturaCaixa;
    }

    public void setAlturaCaixa(int alturaCaixa) {
        this.alturaCaixa = alturaCaixa;
    }

    public double getComprimentoCaixa() {
        return ComprimentoCaixa;
    }

    public void setComprimentoCaixa(int comprimentoCaixa) {
        ComprimentoCaixa = comprimentoCaixa;
    }
    //endregion
}
