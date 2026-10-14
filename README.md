# PetAdote

# Sistema para gestão de uma ONG de adoção de animais

Sistema para gestão de uma ONG de adoção de animais, controlando os animais disponíveis, abrigos/lares temporários, candidatos à adoção e todo o processo de adoção — da candidatura até o acompanhamento pós-adoção. O sistema garante que nenhum animal seja adotado por mais de uma pessoa simultaneamente e que exista um período mínimo de acompanhamento após a entrega do animal.

Como diferencial, o cadastro de cada animal é enriquecido com informações reais de raça (porte, peso, temperamento) obtidas de APIs públicas de raças de cães e gatos, ajudando o adotante a entender melhor as características do animal antes de adotar.

## Requisitos Funcionais

### 1. Cadastro de Animais e Abrigos

* REQ01: Cadastrar abrigos/lares temporários (nome, endereço, capacidade de animais)
* REQ02: Cadastrar animais vinculados a um abrigo, com herança para Cão, Gato e Outro (cada um com atributos próprios, ex.: Cão possui campo de raça)
* REQ03: Buscar informações de porte, peso e temperamento da raça usando as APIs públicas TheDogAPI (cães) e TheCatAPI (gatos) — gratuitas, mas exigem uma chave de acesso obtida por cadastro simples e sem custo nos respectivos sites

### 2. Candidatos à Adoção

* REQ04: Cadastrar adotantes (nome, CPF, endereço, quantidade de animais já possuídos)
* REQ05: Consultar histórico de processos de adoção de um adotante

### 3. Processo de Adoção

* REQ06: Abrir processo de adoção vinculando animal e adotante, com status inicial "Em Avaliação"
* REQ07: Registrar avaliação prévia (composição), incluindo parecer do responsável do abrigo, antes de aprovar o processo
* REQ08: Concluir o processo de adoção, alterando o status do animal para "Adotado"

### 4. Acompanhamento Pós-Adoção

* REQ09: Agendar visitas de acompanhamento pós-adoção (composição), vinculadas ao processo concluído
* REQ10: Registrar o resultado de cada visita (situação do animal, observações)

### 5. Relatórios

* REQ11: Relatório de animais disponíveis por abrigo e espécie
* REQ12: Relatório de adoções concluídas por período, exportável em CSV
* REQ13: Listar processos de adoção pendentes de avaliação há mais de 15 dias

### 6. Regras e Restrições

* REQ14: Bloquear a abertura de um novo processo de adoção para um animal que já esteja com status "Adotado" ou com outro processo "Em Avaliação"
* REQ15: Impedir a conclusão de um processo de adoção sem ao menos uma avaliação prévia registrada com parecer favorável
* REQ16: Não permitir que um adotante tenha mais de 3 processos de adoção concluídos simultaneamente ativos
* REQ17: Bloquear a conclusão de adoção de um animal que esteja com status "Em Tratamento Veterinário"
* REQ18: Garantir que todo processo concluído tenha ao menos 2 visitas de acompanhamento agendadas nos 90 dias seguintes
* REQ19: Validar que a capacidade do abrigo não seja excedida ao registrar um novo animal
* REQ20: Bloquear a exclusão de um abrigo que ainda possua animais vinculados a ele

## Possíveis APIs/Bibliotecas

JavaFX, TheDogAPI (https://www.thedogapi.com — requer chave gratuita via cadastro) e TheCatAPI (https://www.thecatapi.com — requer chave gratuita via cadastro), como alternativa mais simples e sem qualquer chave é possível usar a Dog CEO API (https://dog.ceo/dog-api — sem chave) apenas para fotos/lista de raças, `java.net.http.HttpClient`, Jackson/Gson, Java Time API, JUnit.

Requisito bônus (opcional, fora da contagem oficial): gerar uma página HTML estática tipo "vitrine" com os animais disponíveis para adoção (foto/raça vindos da API), aberta automaticamente no navegador via `Desktop.getDesktop().browse()`.

## Integrantes do grupo com nome completo
* Ana Clara de Lira Florentino e Silva - ana.florentino@ufrpe.br
* Ana Helena de Souza Macedo - ana.helenamacedo@ufrpe.br
* Júlia Lavínia Rodrigues Pedrosa de Carvalho - julia.lavinia@ufrpe.br
* Laura da Costa Corte Cordeiro - laura.corte@ufrpe.br
* Maria Gabriella Macedo Cardoso - maria.gabriellamacedo@ufrpe.br

## Diagrama de classes em UML

 ![Diagrama de Classes](diagrama/PetAdote-Página-1.drawio.png) 

 ## Modelo navegacional

 ![Modelo Navegacional](modelo_navegacional_petadote_final(1).png)


 ## Link

 [https://github.com/lauraccort/PetAdote.git](https://github.com/lauraccort/PetAdote.git)

 
