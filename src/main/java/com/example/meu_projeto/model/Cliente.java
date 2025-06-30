package com.example.meu_projeto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O nome não pode ser nulo.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    private String nome;

    @NotNull(message = "O email não pode ser nulo.")
    @Email(message = "O email deve ser válido.")
    private String email;

    @NotNull(message = "O Gênero não pode ser nulo.")
    private String genero;

    @NotNull(message = "A data de nascimento não pode ser nulo.")
    private String dataNascimento;

    @NotNull(message = "O cpf não pode ser nulo.")
    private String cpf;

    @NotNull(message = "O numero de telefone não pode ser nulo.")
    private String telefone;

    @NotNull(message = "A senha não pode ser nula.")
    @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres.")
    private String senha;

    @Column(name = "tipo_residencia")
    private String tipoResidencia; // Tipo de residência (Casa, Apartamento, etc.)

    @Column(name = "logradouro")
    private String logradouro; // Logradouro

    @Column(name = "numero")
    private String numero; // Número

    @Column(name = "bairro")
    private String bairro; // Bairro

    @Column(name = "cep")
    private String cep; // CEP

    @Column(name = "cidade")
    private String cidade; // Cidade

    @Column(name = "estado")
    private String estado; // Estado

    @Column(name = "pais")
    private String pais; // País

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "data_alteracao")
    private LocalDateTime dataAlteracao;

    // Novos campos
    @Column(name = "endereco_entrega")
    private String enderecoEntrega;

    @Column(name = "descricao_endereco")
    private String descricaoEndereco;

    @Column(name = "numero_cartao")
    private String numeroCartao;

    @Column(name = "bandeira")
    private String bandeira;

    @Column(name = "codigo_seguranca")
    private String codigoSeguranca;

    @Column(name = "nome_cartao")
    private String nomeCartao;


    // Construtores, getters e setters

    public Cliente() {
        this.dataCadastro = LocalDateTime.now();
        this.dataAlteracao = null;  // Inicializa a data de alteração como null
    }

    public Cliente(String nome, String email, String genero, String dataNascimento, String cpf, String telefone, String senha, String tipoResidencia, 
                   String logradouro, String numero, String bairro, String cep, 
                   String cidade, String estado, String pais,
                   String enderecoEntrega, String descricaoEndereco, String numeroCartao,
                   String bandeira, String codigoSeguranca, String nomeCartao) {
        this.nome = nome;
        this.email = email;
        this.email = genero;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
        this.telefone = telefone;
        this.senha = senha;
        this.tipoResidencia = tipoResidencia;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
        this.enderecoEntrega = enderecoEntrega;
        this.descricaoEndereco = descricaoEndereco;
        this.numeroCartao = numeroCartao;
        this.bandeira = bandeira;
        this.nomeCartao = nomeCartao;
        this.codigoSeguranca = codigoSeguranca;
        this.dataCadastro = LocalDateTime.now();
        this.dataAlteracao = null;  // Inicializa a data de alteração como null
    }

    // Getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }


    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoResidencia() {
        return tipoResidencia;
    }

    public void setTipoResidencia(String tipoResidencia) {
        this.tipoResidencia = tipoResidencia;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public String getDescricaoEndereco() {
        return descricaoEndereco;
    }

    public void setDescricaoEndereco(String descricaoEndereco) {
        this.descricaoEndereco = descricaoEndereco;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getBandeira() {
        return bandeira;
    }

    public void setBandeira(String bandeira) {
        this.bandeira = bandeira;
    }

    public String getCodigoSeguranca() {
        return codigoSeguranca;
    }

    public void setCodigoSeguranca(String codigoSeguranca) {
        this.codigoSeguranca = codigoSeguranca;
    }
    public String getNomeCartao() {
        return nomeCartao;
    }

    public void setNomeCartao(String nomeCartao) {
        this.nomeCartao = nomeCartao;
    }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nome='" + nome + "', email='" + email + "', senha='****', " +
                "tipoResidencia='" + tipoResidencia + "', logradouro='" + logradouro + "', numero='" + numero + "', " +
                "bairro='" + bairro + "', cep='" + cep + "', cidade='" + cidade + "', estado='" + estado + "', pais='" + pais + "'}";
    }
}
