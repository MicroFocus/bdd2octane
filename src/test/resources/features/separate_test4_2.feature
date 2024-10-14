@TID4001REV0.3.0 @SomaOrcamento @NovaVistoria @Gabriel @Gambatti

Feature: Nova Vistoria
  Rule: initialize
  Scenario: Validar não exibição do botão Nova Vistoria para perfis diferentes de Orçamentista Oficina
  Given que o usuário esteja logado no sistema com cpf "53133447068"
    Then não visualiza o botão Nova Vistoria