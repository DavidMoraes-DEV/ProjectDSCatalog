package com.devsuperior.dscatalog.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.services.CategoryService;

@RestController
@RequestMapping(value = "/categories") // -> Podemos colocar qual a rota REST do recurso. Adotar padrão do recurso no plural
public class CategoryResource { 
	
	//Cria uma DEPENDENCIA com o CategoryService
	@Autowired
	private CategoryService service;
	
	//Primeiro EndPoint, ou seja, a primeira rota que responderá alguma coisa
	@GetMapping
	public ResponseEntity<List<CategoryDTO>> findAll() { //findAll -> Busca todas as categorias
		
		//Aqui chama o SERVICE que chama o REPOSITORY e ele vai la no banco de dados e traz os objetos, intancia todos eles traz pra ca e guarda nessa lista que é retornada pelo método
		List<CategoryDTO> list = service.findAll();
		
		return ResponseEntity.ok().body(list); //Retorna a lista no corpo da resposta HTTP dessa requisição. Para Instanciar o ResponseEntity utilizando os builders dele
	}
}
/*
**************PRIMEIRO RECURSO REST DA APLICAÇÃO****************

* RESOURCE:
	- Implementa o controlador REST

* API = Aplication Programe Interface
	- São os recursos que serão disponibilizados para as aplicações utilizarem (WEB, MOBILE, ETC...)
	- A API é implementada por meio dos CONTROLADORES REST
		- O termo conceitual para esses recursos é RESOURCE
		- É como se os recursos fosse o conceito e os controladores a forma de se implementar esses conceitos
		
* CONVENÇÃO DE NOMES = CategoryResourse
	- Por convenção sempre que um recurso tem a ver com uma entidade, se coloca o nome da entidade depois a palavra resource(Recurso) = CategoryResource.
	- Isso identifica que será um recurso para essa classe Category que é uma entidade do projeto

* ANNOTATIONS:
	- Para se configurar no Spring que essa classe CategoryResource será um CONTROLADOR REST
		- utiliza-se um annotation: @RestController
	
	- Um annotation é uma forma enxuta e simples de configurar alguma coisa no código, ou seja, algo que já esta implementado(já existe) no Spring
		- Por exemplo os desenvolvedores do SPRING já implementou nesse annotation tudo o que for necessário para transformar uma classe em um recurso REST
		- Então para utilizar a estrutura implementada basta utilizar o annotation @RestController
	
	- O annotation então irá efetuar um pré-processamento em segundo plano no momento da execução para essa classe se tornar um recurso rest e ser disponibilizada como tal
	- Spring utiliza muito annotation para o código ficar mais limpo e não precisar ficar implementando muito lógica de programação de forma manual no código
	
* ENDPOINT:
	- É uma rota que responderá alguma coisa
	- Por exemplo:
		- Nesse recurso /categories teremos várias rotas EndPoint como:
			- Salvar uma categoria
			- Editar uma categoria
			- Buscar uma categoria
			- etc...

* RESPONSE ENTITY:
	- É um Objeto do Spring que encapsula uma resposta HTTP
	- É um tipo Generac
		- Podendo definir entre <> qual é o tipo de dados que terá no corpo da resposta
			- Exemplo: ResponseEntity<Category>
	
	- Para Instanciar o ResponseEntity utiliza-se os próprios builders dele:
		- .ok() -> Ele deixa responder um resposta do tipo 200 que significa que a requisição foi realizada com sucesso
		- .body() -> Define o corpo da resposta que no caso do primeiro EndPoint é a variável list
		- .ok() -> Também aceita a declaração do corpo diretamente por meio de uma sobrecarga
		- No caso do primeiro EndPoint como foi criado uma lista de categorias o tipo do ResponseEntity deverá ser uma lista de categorias também
			- ResponseEntity<List<Category>>
	
 	- Para configurar que o método findAll é um WEB SERVICE/ENDPOINT da classe CATEGORY:
		- Devemos utilizar outro annotation:
			- @GetMapping
			
------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* PARTE I: Por fim nessa aula ir no postman e realizar uma requisição GET: http://localhost:8080/categories com a aplicação executando.
	- No postman terá que aparecer ao executar a requisição as duas categorias que já irá aparecer em formato JSON:
	[
    	{
        	"id": 1,
        	"name": "Books"
    	},
    	{
        	"id": 2,
        	"name": "Eletronics"
    	}
	]
			
------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 
* Integrando o banco de dados H2
 - Será o banco de dados de TESTE que será utilizado nesse projeto inicial do SpringBoot
	
*/