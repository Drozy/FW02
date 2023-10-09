package program.model;

public class PetCreator extends Creator {
    @Override
    protected Pet createNewPet(PetType type) {
        return switch (type) {
            case Cat -> new Cat();
            case Dog -> new Dog();
            case Hamster -> new Hamster();
        };
    }
}
