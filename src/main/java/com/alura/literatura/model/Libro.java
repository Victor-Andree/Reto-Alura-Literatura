package com.alura.literatura.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AutorLibro> autores = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "idiomas", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "idioma")
    private Set<String> libroIdiomas = new HashSet<>();

    public Libro() {
    }

    public Libro(DatosLibros datosLibros){
        this.titulo = datosLibros.titulo();
        this.libroIdiomas = new HashSet<>(datosLibros.idiomas());
        if (datosLibros.autor() !=null){
            for (var autor : datosLibros.autor()){
                this.autores.add(new AutorLibro(autor, this));
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<AutorLibro> getAutores() {
        return autores;
    }

    public void setAutores(List<AutorLibro> autores) {
        this.autores = autores;
    }

    public Set<String> getLibroIdiomas() {
        return libroIdiomas;
    }

    public void setLibroIdiomas(Set<String> libroIdiomas) {
        this.libroIdiomas = libroIdiomas;
    }

    @Override
    public String toString() {
        var autoresSTR = autores.stream()
                .map(AutorLibro::getNombre)
                .reduce((a, b) -> a + "," + b)
                .orElse("NA");

        var idiomaSTR = String.join(",", libroIdiomas);

        return String.format("---%n" +
                        "%-10s: %s%n" +
                        "%-10s: %s%n" +
                        "%-10s: %s%n",
                "TITULO", titulo,
                "AUTOR", autoresSTR,
                "IDIOMA", idiomaSTR
        );
}
}