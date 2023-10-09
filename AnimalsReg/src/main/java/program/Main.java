package program;

import program.controller.PetController;
import program.model.Pet;
import program.services.IRepository;
import program.services.PetRepository;
import program.view.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        IRepository<Pet> animals = new PetRepository();
        PetController controller = new PetController(animals);
        new ConsoleMenu(controller).start();
    }
}