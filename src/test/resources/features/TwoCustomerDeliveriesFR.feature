#language: fr

Fonctionnalité: Drone Delivery
  Contexte: Le fonctionnement de l'entreprise

  Scénario: L'entreprise enregistre des colis et des fournisseurs
    Quand Un employé enregistre un colis de numéro 200 de 5kg d'un fournisseur du nom de AUG
    Et un autre colis de numéro 600 de 3kg d'un fournisseur de nom PAU
    Et un autre cois de numéro 1000 de 6kg du fournisseur AUG

  Scénario: L'entreprise enregistre des livraisons
    Quand un employé reçoit l'appel de madame Donélia Monin résidant à l'adresse Nice
    Alors il vérifie si elle existe déjà dans le système et l'enregistre si ce n'est pas le cas
    Et à la demande du client enregistre une livraison le 12/06/2020 à 10h00 pour le colis 200
    Alors il y a 1 colis à livrer

  Scénario: L'entreprise enregistre des livraisons
    Quand l'employé reçoit l'appel de madame Donélia Monin
    Alors il remarque donc que cette dernière est dans le système
    Et à sa demande, il enregistre une livraison le 12/06/2020 à 10h00 pour le colis 1000
    Alors l'employé explique cela n'est pas possible
    Et donc la cliente reprogramme sa livraison pour le 13/06/2020 à 10h00 pour le colis 1000
    Alors il y a maintenant 2 colis à livrer

  Scénario: L'entreprise enregistre des livraisons
    Quand l'employé est contacté par madame Aurore Lapraresseuse résidant à l'adresse 1 Rue de la paresse
    Alors il constate qu'elle n'est pas dans le système et donc l'enregistre
    Et cette dernière demande à être livrée le 12/06/2020 à 12h00 pour le colis 450
    Alors l'employé lui dit que le colis 450 n'existe pas
    Alors elle change le numéro du colis en 600
    Alors l'employé enregistre la livraison
    Et il y a 3 livraisons à effectuer

  Scénario: Livrasions
    Quand le 12/06/2020 l'employé demande la prochaine livraison
    Alors il livre le colis au nuémro 200

   Scénario: Changement d'horaires
     Quand Madame Donélia Monin rappelle le 12/06/2020 pour reprogrammer sa livraison
     Alors elle donne la date du 14/06/2020 à 10h00 de son colis 1000
     Et l'employé reprogramme sa livraison

   Scénario: Livraisons
     Quand l'employé refait une autre livraison
     Alors il livre celui au numéro 600

   Scénario: Gestion des factures
     Quand un employé génère les factures de la journées du 12/06/2020
     Alors il y a 2 factures éditées
     Et 1 pour le fournisseur AUG de 50€
     Et PAU en a 1 de 30€

    Scénario: Livraison
      Quand le 14/06/2020 on demande la prochaine livraison
      Alors on livre celle au colis numéro 1000
      Et donc à la fin de la journée le fournisseur AUG a 2 factures à payer