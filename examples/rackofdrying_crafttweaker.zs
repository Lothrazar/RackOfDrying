// https://docs.blamejared.com/

var dryingRack = <recipetype:rackofdrying:drying>;

// delete recipe example
// show advanced tooltips in minecraft and use JEI is one way to see these IDs
dryingRack.removeRecipe("rackofdrying:tipped_arrow");

// add recipes.
// IMPORTANT: the name must be unique
// signature is: addRecipe(name, input, output, dryTimeInTicks)
dryingRack.addRecipe("charcoal_test", <item:minecraft:coal>, <item:minecraft:charcoal> * 4, 100);

dryingRack.addRecipe("gravel_test", <item:minecraft:gravel>, <item:minecraft:sand>, 200);
