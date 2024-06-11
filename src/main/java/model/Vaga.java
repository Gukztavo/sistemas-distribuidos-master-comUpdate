package model;

import jakarta.persistence.*;
import java.io.Serializable;

import java.util.List;

@Entity
@Table(name = "vaga")
public class Vaga implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "email")
//    private String email;
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }

    @Column(name = "nome")
    private String nome;

    @Column(name = "faixaSalarial")
    private double faixaSalarial;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "estado")
    private String estado;

    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email")
    private Empresa empresa;

    @ElementCollection
    @CollectionTable(name = "vaga_competencia", joinColumns = @JoinColumn(name = "vaga_id"))
    @Column(name = "competencia")
    private List<String> competencias;

    // Getters and Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getCompetencias() {
        return competencias;
    }

    public void setCompetencias(List<String> competencias) {
        this.competencias = competencias;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getFaixaSalarial() {
        return faixaSalarial;
    }

    public void setFaixaSalarial(double faixaSalarial) {
        this.faixaSalarial = faixaSalarial;
    }
}
