// Base class
class Animal {
    constructor(name) {
        this.name = name;
    }

    speak() {
        console.log(`${this.name} makes a noise.`);
    }
}

// Class inheriting from Animal
class Mammal extends Animal {
    constructor(name, hasFur) {
        super(name);
        this.hasFur = hasFur;
    }

    speak() {
        console.log(`${this.name} says, "I am a mammal."`);
    }
}

// Class inheriting from Mammal
class Dog extends Mammal {
    constructor(name, breed) {
        super(name, true);
        this.breed = breed;
    }

    speak() {
        console.log(`${this.name} barks.`);
    }
}

// Another class inheriting from Mammal
class Cat extends Mammal {
    constructor(name, isIndependent) {
        super(name, true);
        this.isIndependent = isIndependent;
    }

    speak() {
        console.log(`${this.name} meows.`);
    }
}

// Class inheriting from Dog
class Puppy extends Dog {
    constructor(name, breed) {
        super(name, breed);
        this.age = 'puppy';
    }

    speak() {
        console.log(`${this.name} yips.`);
    }
}

// Example usage
const charlie = new Animal('Charlie');
const max = new Mammal('Max', true);
const buddy = new Dog('Buddy', 'Golden Retriever');
const whiskers = new Cat('Whiskers', true);
const bella = new Puppy('Bella', 'Beagle');

charlie.speak();
max.speak();
buddy.speak();
whiskers.speak();
bella.speak();
