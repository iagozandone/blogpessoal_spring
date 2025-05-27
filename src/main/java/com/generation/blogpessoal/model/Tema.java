package com.generation.blogpessoal.model;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity // Indica que a classe será mapeada para uma tabela no banco de dados
@Table(name = "tb_temas") // Nome da tabela no banco de dados
public class Tema {

    @Id // Indica a chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto incremento do ID
    private Long id;

    @NotBlank(message = "O atributo descrição é obrigatório!") // Não pode ser nulo ou vazio
    @Size(min = 5, max = 100, message = "A descrição deve conter entre 5 e 100 caracteres.")
    private String descricao;

    @OneToMany(fetch = FetchType.EAGER ,mappedBy = "tema", cascade = CascadeType.REMOVE) // Um Tema pode ter várias Postagens
    @JsonIgnoreProperties("tema") // Evita recursividade ao serializar JSON
    private List<Postagem> postagens;
    
    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Postagem> getPostagens() {
        return postagens;
    }

    public void setPostagens(List<Postagem> postagens) {
        this.postagens = postagens;
    }
}
