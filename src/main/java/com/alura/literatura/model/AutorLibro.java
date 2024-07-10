package com.alura.literatura.model;


import jakarta.persistence.*;

@Entity
@Table(name = "autores")
public class AutorLibro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Integer nacimiento;
    private Integer muerte;

    @ManyToOne
    @JoinColumn(name = "libro_id")
    private Libro libro;

    public AutorLibro() {
    }

    public AutorLibro(DatosAutor datos, Libro libro) {
        this.nombre = datos.nombre();
        this.nacimiento = datos.fechaDeNacimiento();
        this.muerte = datos.muerte();
        this.libro = libro;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getMuerte() {
        return muerte;
    }

    public void setMuerte(Integer muerte) {
        this.muerte = muerte;
    }

    public Integer getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(Integer nacimiento) {
        this.nacimiento = nacimiento;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    @Override
    public String toString() {
        return "AutorLibro{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", nacimiento=" + nacimiento +
                ", muerte=" + muerte +
                '}';
    }
}

