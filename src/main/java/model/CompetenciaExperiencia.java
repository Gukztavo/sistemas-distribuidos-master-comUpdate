package model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "competencia_experiencia")
public class CompetenciaExperiencia implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "competencia")
    private String competencia;

    @Column(name = "experiencia")
    private int experiencia;

    @ManyToOne
    @JoinColumn(name = "emailCandidato", nullable = false, referencedColumnName = "email")
    private Pessoa candidato;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }

    public Pessoa getCandidato() {
        return candidato;
    }

    public void setCandidato(Pessoa candidato) {
        this.candidato = candidato;
    }
}
