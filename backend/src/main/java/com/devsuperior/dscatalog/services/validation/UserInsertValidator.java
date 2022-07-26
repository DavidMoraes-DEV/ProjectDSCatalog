package com.devsuperior.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;

//Parametriza o tipo da annotation(UserInsertValid) e o tipo da classe que vai receber esse annotation(UserInsertDTO)
public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {
	
	@Autowired
	private UserRepository repository;
	
	@Override
	public void initialize(UserInsertValid ann) { //Coloca-se aqui alguma lógica para quando for inicializar um objeto(Nesse projeto ficará em branco)
	}

	@Override
	public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) { //Esse método testa se o objeto (UserInsertDTO) é válido ou não
		
		List<FieldMessage> list = new ArrayList<>();
		
		//Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista
		User user = repository.findByEmail(dto.getEmail());
		if(user != null) { //Teste para ver se o email vindo do UserInsertDTO já existe no banco e caso existir insere a mensagem de erro que o email já existe
			list.add(new FieldMessage("email", "Email já existe"));
		}
		
		for (FieldMessage e : list) { //Insere na lista de erros do beanValidation os erros se gerados
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}