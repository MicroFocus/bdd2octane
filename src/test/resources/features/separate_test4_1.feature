@TID8002REV0.4.0 @SomaOrcamento @ExibirSelecionarEmpresaResponsavel @Gabriel @Gambatti
Feature: Selecionar empresa responsavel
  Rule: initialize
  Scenario: Mostrar empresa responsável
    Given que o usuário esteja logado no sistema com cpf "45849905065"
    And o usuário navega até a 'página Meu Roteiro'
    And acesso um card de sinistro
    And o sistema deve exibir o nome da empresa responsável
    Then deve exibir uma lista selecionável conforme cadastro do usuário