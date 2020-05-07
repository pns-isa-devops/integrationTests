#language: fr

  Fonctionnalité: Drone Delivery
    Contexte: Le fonctionnement de l'entreprise

      Scénario: L'entreprise n'a rien à livrer
        Quand L'employé demande la prochaine livraison
        Alors il y a 0 livraisons à livrer

        Scénario: L'entreprise doit livrer un colis
          Quand Un employé enregistre un colis d'un fournisseur du nom de AUG
          Et un client du nom de Paul Koffi avec l'adresse 903 routes des Colles
          Et appelle pour programmer sa livraison le 12/06/2020 à 10h00 du colis 2020
          Alors il y a 1 livraison à faire

         Scénario: L'entreprise doit livrer un colis
           Quand le 12/06/2020 un employé demande la prochaine livraison
           Alors il y 1 facture à payer pour le fournisseur AUG
