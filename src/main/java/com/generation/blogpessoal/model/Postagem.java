package com.generation.blogpessoal.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity // Indica que a classe é uma entidade do JPA (será mapeada para uma tabela no banco de dados)
@Table(name = "tb_postagens") // Define o nome da tabela no banco de dados como "tb_postagens"
public class Postagem {
	
	@Id // Primary Key - Indica que o atributo "id" é a chave primária da tabela
	@GeneratedValue(strategy = GenerationType.IDENTITY) //AUTO_INCREMENT - Define que o valor do "id" será gerado automaticamente com auto incremento
	private Long id;
	
	@Column (length = 100)     // Define que a coluna "titulo" terá no máximo 100 caracteres
	@NotBlank(message = "O atributo título é obrigatório!")     // Validação: o campo "titulo" não pode estar em branco
	@Size(min = 5, max = 100, message = "O atributo título deve ter no minimo 5 e no máximo 100 caracteres.")     // Validação: o "titulo" deve ter no mínimo 5 e no máximo 100 caracteres
	private String titulo;
	
	@Column (length = 1000)    // Define que a coluna "texto" terá no máximo 1000 caracteres
	@NotBlank(message = "O atributo texto é obrigatório!")     // Validação: o campo "texto" não pode estar em branco
	@Size(min = 10, max = 100, message = "O atributo texto deve ter no minimo 10 e no máximo 1000 caracteres.")     // Validação: o "texto" deve ter no mínimo 10 e no máximo 1000 caracteres
	private String texto;
	
    // Anotação que atualiza automaticamente a data com a hora atual sempre que a entidade for modificada
	@UpdateTimestamp
	private LocalDateTime data;

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

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}
	
	
}
