package com.generation.blogpessoal.controller;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
 
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
 
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import com.generation.blogpessoal.util.TestBuilder;
 
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
 
public class UsuarioControllerTest {
 
	@Autowired
 
	private TestRestTemplate testRestTemplate;
 
	@Autowired
 
	private UsuarioService usuarioService;
 
	@Autowired
 
	private UsuarioRepository usuarioRepository;
 
	private static final String USUARIO_ROOT_EMAIL = "root@email.com";
 
	private static final String USUARIO_ROOT_SENHA = "rootroot";
 
	private static final String BASE_URL_USUARIOS = "/usuarios";
 
	@BeforeAll
	void start() {
 
		usuarioRepository.deleteAll();
 
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuarioRoot());
 
	}
 
	@Test
	@DisplayName("Deve cadastrar um novo usuário com sucesso")
	public void deveCadastrarUsuario() {
 
		// Given
		Usuario usuario = TestBuilder.criarUsuario(null, "Renata Negrini", "renata_negrini@email.com", "12345678");
 
		// When
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
 
		// Then
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertEquals("Renata Negrini", resposta.getBody().getNome());
		assertEquals("renata_negrini@email.com", resposta.getBody().getUsuario());
 
	}
	
	@Test
	@DisplayName(" Não Deve cadastrar um novo usuário com sucesso")
	public void naodeveCadastrarUsuario() {
 
		// Given
		Usuario usuario = TestBuilder.criarUsuario(null, "Angelo dos Santos", "angelo@email.com", "12345678");
		usuarioService.cadastrarUsuario(usuario);
 
		// When
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL_USUARIOS + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
 
		// Then
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
 
	}
	
	@Test
	@DisplayName(" Deve atualizar os dados de um usuário com sucesso")
	public void deveAtualizarUmUsuario() {
 
		// Given
		Usuario usuario = TestBuilder.criarUsuario(null, "Giovana Lucia", "giovana_lucia@email.com", "12345678");
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);
		
		Usuario usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(),"Giovana Lucia Freita", "giovana_luciafreitas@email.com", "12345678");
		
		// When
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuarioUpdate);
		ResponseEntity<Usuario> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_USUARIOS + "/atualizar", HttpMethod.PUT, requisicao, Usuario.class);
		
		// Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals("Giovana Lucia Freita", resposta.getBody().getNome());
		assertEquals("giovana_luciafreitas@email.com", resposta.getBody().getUsuario());
		
	}
	
	@Test
	@DisplayName(" Deve listar todos os usuários com sucesso")
	public void deveListarUmUsuario() {
		//Given
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Jovani Almeida","jovani_almeida@email.com", "senha123"));
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Carlos Garcia","carlos_garcia@email.com", "senha123"));

		//When
		ResponseEntity<Usuario[]> resposta = testRestTemplate
				.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA)
				.exchange(BASE_URL_USUARIOS + "/all", HttpMethod.GET, null, Usuario[].class);
		//Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	// NOVO TESTE: Encontrar usuário por ID
		@Test
		@DisplayName("Deve encontrar um usuário por ID com sucesso")
		public void deveEncontrarUsuarioPorId() {
			// Given
			Usuario usuarioParaTeste = TestBuilder.criarUsuario(null, "Laura Mendes", "laura_mendes@email.com", "laura123");
			Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuarioParaTeste);
			assertTrue(usuarioCadastrado.isPresent(), "Usuário de teste não foi cadastrado.");
			Long idUsuario = usuarioCadastrado.get().getId();

			// When
			ResponseEntity<Usuario> resposta = testRestTemplate
					.withBasicAuth(USUARIO_ROOT_EMAIL, USUARIO_ROOT_SENHA) // Autenticação necessária para buscar por ID
					.exchange(BASE_URL_USUARIOS + "/" + idUsuario, HttpMethod.GET, null, Usuario.class);

			// Then
			assertEquals(HttpStatus.OK, resposta.getStatusCode());
			assertNotNull(resposta.getBody());
			assertEquals(idUsuario, resposta.getBody().getId());
			assertEquals("Laura Mendes", resposta.getBody().getNome());
			assertEquals("laura_mendes@email.com", resposta.getBody().getUsuario());
		}

	    // NOVO TESTE: Logar usuário
		@Test
		@DisplayName("Deve logar um usuário com sucesso e retornar token")
		public void deveLogarUsuario() {
			// Given
	        // Cria um usuário específico para o teste de login, diferente do root para evitar conflitos
			String emailLogin = "teste_login@email.com";
			String senhaLogin = "senhaLogin123";
			usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Usuario Login Teste", emailLogin, senhaLogin));

			UsuarioLogin usuarioLogin = new UsuarioLogin(); // Supondo que UsuarioLogin tenha setters ou um construtor
			usuarioLogin.setUsuario(emailLogin);
			usuarioLogin.setSenha(senhaLogin);
	        // Se tiver construtor: new UsuarioLogin(emailLogin, senhaLogin, "", ""); ou similar

			// When
			HttpEntity<UsuarioLogin> requisicao = new HttpEntity<UsuarioLogin>(usuarioLogin);
			ResponseEntity<UsuarioLogin> resposta = testRestTemplate
					.exchange(BASE_URL_USUARIOS + "/logar", HttpMethod.POST, requisicao, UsuarioLogin.class);

			// Then
			assertEquals(HttpStatus.OK, resposta.getStatusCode());
			assertNotNull(resposta.getBody());
			assertNotNull(resposta.getBody().getToken(), "Token não deve ser nulo após o login.");
	        assertTrue(resposta.getBody().getToken().length() > 10, "Token parece ser inválido ou muito curto."); // Verifica se o token tem um tamanho razoável
			assertEquals(emailLogin, resposta.getBody().getUsuario());
	        // Você pode adicionar mais asserts para outros campos do UsuarioLogin, se houver (ex: nome, foto)
		}

	    // Teste para login com usuário inexistente
		@Test
		@DisplayName("Não deve logar usuário inexistente")
		public void naoDeveLogarUsuarioInexistente() {
			// Given
			UsuarioLogin usuarioLogin = new UsuarioLogin();
			usuarioLogin.setUsuario("inexistente@email.com");
			usuarioLogin.setSenha("senhaqualquer");

			// When
			HttpEntity<UsuarioLogin> requisicao = new HttpEntity<UsuarioLogin>(usuarioLogin);
			ResponseEntity<UsuarioLogin> resposta = testRestTemplate
					.exchange(BASE_URL_USUARIOS + "/logar", HttpMethod.POST, requisicao, UsuarioLogin.class);

			// Then
			assertEquals(HttpStatus.UNAUTHORIZED, resposta.getStatusCode(), "Deveria retornar UNAUTHORIZED para usuário inexistente.");
		}

	    // Teste para login com senha incorreta
		@Test
		@DisplayName("Não deve logar usuário com senha incorreta")
		public void naoDeveLogarUsuarioComSenhaIncorreta() {
			// Given
			String emailExistente = "senha_incorreta@email.com";
			String senhaCorreta = "correta123";
			String senhaIncorreta = "incorreta456";
			usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Teste Senha", emailExistente, senhaCorreta));

			UsuarioLogin usuarioLogin = new UsuarioLogin();
			usuarioLogin.setUsuario(emailExistente);
			usuarioLogin.setSenha(senhaIncorreta);

			// When
			HttpEntity<UsuarioLogin> requisicao = new HttpEntity<UsuarioLogin>(usuarioLogin);
			ResponseEntity<UsuarioLogin> resposta = testRestTemplate
					.exchange(BASE_URL_USUARIOS + "/logar", HttpMethod.POST, requisicao, UsuarioLogin.class);

			// Then
			assertEquals(HttpStatus.UNAUTHORIZED, resposta.getStatusCode(), "Deveria retornar UNAUTHORIZED para senha incorreta.");
	}
} 