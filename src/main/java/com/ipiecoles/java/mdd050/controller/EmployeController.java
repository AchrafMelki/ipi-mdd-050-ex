package com.ipiecoles.java.mdd050.controller;

import com.ipiecoles.java.mdd050.exception.ConflictException;
import com.ipiecoles.java.mdd050.exception.EmployeException;
import com.ipiecoles.java.mdd050.model.Employe;
import com.ipiecoles.java.mdd050.service.EmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employes")
public class EmployeController {

    @Autowired
    private EmployeService employeService;

    /**
     * Permet de récupérer le nombre d'employés total
     * @return le nombre d'employés total contenus dans la table Employe
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Long count(){
        return employeService.countAllEmploye();
    }

    /**
     * Permet de récupérer les informations d'un employé à partir de son identifiant technique
     *
     * @param id Identifiant technique de l'employé
     * @return l'employé si l'identifiant est trouvé ou une erreur 404 sinon.
     */
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @RequestMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Employe findById(@PathVariable(value = "id") Long id){
        return employeService.findById(id);
    }

    /**
     * Permet de récupérer les informations d'un employé à partir de son matricule
     * @param matricule Le matricule de l'employé (C00001 ou T00002 ou M00003 par exemple)
     * @return l'employé si le matricule est trouvé ou une erreur 404 sinon.
     */
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, params = "matricule")
    public Employe findByMatricule(@RequestParam("matricule") String matricule){
        return employeService.findByMatricule(matricule);
    }

    /**
     * Permet de récupérer les employés de manière paginée et triée
     *
     * @param page Numéro de la page en partant de 0
     * @param size Taille de la page
     * @param sortDirection Tri ascendant ASC ou descendant DESC
     * @param sortProperty Propriété utilisée par le tri
     * @return Une page contenant les employés
     */
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public Page<Employe> findAll(@RequestParam("page") Integer page, @RequestParam("size") Integer size, @RequestParam("sortDirection") String sortDirection, @RequestParam("sortProperty") String sortProperty){
        return employeService.findAllEmployes(page, size, sortProperty, sortDirection);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, value = "")
    public Employe createEmploye(@RequestBody Employe employe) throws ConflictException {
        try {
            return this.employeService.creerEmploye(employe);
        }
        catch (DataIntegrityViolationException e){
            if(e.getMessage().contains("matricule_unique")){
                throw new ConflictException("L'employé de matricule " + employe.getMatricule() + " existe déjà !");
            }
            throw new IllegalArgumentException("Une erreur technique est survenue");
        }
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public Employe updateEmploye(@PathVariable("id") Long id, @RequestBody Employe employe){
        return this.employeService.updateEmploye(id,employe);
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteEmploye(@PathVariable("id") Long id) throws EmployeException {
            this.employeService.deleteEmploye(id);
    }

    /*@ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException entityNotFoundException) {
        return "ERREUR !!!";
    }*/

}
