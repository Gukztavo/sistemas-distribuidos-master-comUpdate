package model;

import jakarta.persistence.Entity;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity(name = "empresa")

@Table(name = "empresa")
public class Empresa implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;


    private String razaoSocial;

    private String emailEmpresa;

    private String cnpj;

    private String senha;

    private String descricao;

    private String ramo;

    public Empresa(String ramo, String descricao, String senha, String cnpj, String emailEmpresa, String razaoSocial) {
        this.ramo = ramo;
        this.descricao = descricao;
        this.senha = senha;
        this.cnpj = cnpj;
        this.emailEmpresa = emailEmpresa;
        this.razaoSocial = razaoSocial;

    }
    public Empresa() {
    }


// Getters e setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getEmailEmpresa() {
        return emailEmpresa;
    }

    public void setEmailEmpresa(String emailEmpresa) {
        this.emailEmpresa = emailEmpresa;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getRamo() {
        return ramo;
    }

    public void setRamo(String ramo) {
        this.ramo = ramo;
    }
}
