package net.mat0u5.lifeseries.seasons.season.wildlife.wildcards.wildcard.trivia;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.mat0u5.lifeseries.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TriviaQuestionManager {

    private static final String DEFAULT_EASY_TRIVIA = "[\n" +
"    {\"question\": \"Who was the first player to go yellow in Secret Life?\", \"answers\": [\"Jimmy\", \"Martyn\", \"Lizzie\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Which players were part of a team called the Mounders in Secret Life?\", \"answers\": [\"Bdubs, Pearl, and Mumbo\", \"Joel, Bdubs, Pearl, and Mumbo\", \"Lizzie, Bdubs, Pearl, and Mumbo\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"What disc was playing while Grian, Martyn, and Scar were watching the Relation-Ship burn down?\", \"answers\": [\"Mellohi\", \"Otherside\", \"Pigstep\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Which one of these is NOT a paper Scar passed around in Third Life?\", \"answers\": [\"No Kill Pass\", \"Free Sand Pass\", \"Free Kill Pass\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Which of these life colors was NOT allowed to kill during Limited Life without Boogeyman?\", \"answers\": [\"Green\", \"Yellow\", \"Red\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Who won Secret Life?\", \"answers\": [\"Scar\", \"Pearl\", \"Gem\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"At the end of Secret Life, Gem had to fight against multiple players alone. The fight was a...\", \"answers\": [\"2v1\", \"3v1\", \"4v1\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Which pair was out first in Double Life?\", \"answers\": [\"Jimmy and Tango\", \"Joel and Etho\", \"Grian and Scar\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"What did the Southlanders say when using a spyglass in Last Life?\", \"answers\": [\"Aha!\", \"Oho!\", \"Hee-hee!\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Who was Etho's favorite son in Limited Life?\", \"answers\": [\"Bdubs\", \"Scar\", \"He didn't play favorites\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"In Secret Life, who failed the 100-block-high water bucket clutch?\", \"answers\": [\"Grian\", \"Joel\", \"Martyn\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"In Limited Life, which player went AFK for a whole session because they were sick?\", \"answers\": [\"Pearl\", \"Grian\", \"Cleo\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"What did Scott and Martyn call themselves in Limited Life?\", \"answers\": [\"Mean Gills\", \"LGB-SEA\", \"H2-bros\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"What was the name of Pearl's dog in Double Life?\", \"answers\": [\"Tilly\", \"Milly\", \"Billy\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Which of these was NOT a Life Series Season?\", \"answers\": [\"Second Life\", \"Limited Life\", \"Real Life\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Where did Grian and Mumbo hide their diamonds in Last Life?\", \"answers\": [\"Right here!\", \"In a hidden chest below Mumbo's bunker\", \"In an enderchest\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"In what season did Scar take care of Jellie Pandas?\", \"answers\": [\"Last Life\", \"Double Life\", \"Secret Life\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Of these pairs, who got married in Third Life?\", \"answers\": [\"Ren and Martyn\", \"Etho and Bdubs\", \"Jimmy and Scott\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who was Pearl's soulmate in Double Life?\", \"answers\": [\"Scott\", \"Cleo\", \"Martyn\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"In Third Life, who declared \\\"Red winter is coming!\\\" when they turned red?\", \"answers\": [\"Ren\", \"Martyn\", \"Skizz\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"What was the name of Etho and Joel's base in Double Life?\", \"answers\": [\"The Pirate-ship\", \"The Big Barge\", \"The Relation-ship\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"What season is Team TIES from?\", \"answers\": [\"Last Life\", \"Secret Life\", \"Limited Life\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who was Bdubs' soulmate in Double Life?\", \"answers\": [\"Ren\", \"Etho\", \"Impulse\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"What item was stolen from the front of BigB's Third Life base?\", \"answers\": [\"Enchanter\", \"Giant cookie\", \"Doors\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Which two mobs fought against each other for a task in Secret Life?\", \"answers\": [\"Warden vs Guardian\", \"Warden vs Wither\", \"Wither vs Ender Dragon\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"\\\"The 3 G's\\\" are an alliance consisting of Cleo, Scott, and Pearl. In which season did this alliance form?\", \"answers\": [\"Limited Life\", \"Double Life\", \"Last Life\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Where was the Enchanter located originally in Double Life?\", \"answers\": [\"Spawn\", \"Ancient City\", \"A mangrove forest\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Who won Real Life?\", \"answers\": [\"Scott\", \"Cleo\", \"Pearl\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"What was the name of Gem, Impulse, and Scott's band?\", \"answers\": [\"Gem and the Boys\", \"Cherryblossoms\", \"Gem and the Scotts\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Why was Bdubs' house upside down in Secret Life?\", \"answers\": [\"It was an inside joke with his team\", \"It was his task\", \"He wanted to try something new\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"What happened when you failed a hard task in Secret Life?\", \"answers\": [\"You died\", \"You lost 10 hearts\", \"Nothing happened\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"If I had a nickel for every Life Series Jimmy won, how many nickels would I have?\", \"answers\": [\"1, which isn't a lot, but at least it's something\", \"2, which isn't a lot, but it's weird it happened twice\", \"0, which really means nothing\"], \"correct_answer_index\": 2}\n" +
"]";

    private static final String DEFAULT_MEDIUM_TRIVIA = "[\n" +
"    {\"question\": \"Who was the first person to be eliminated in Real Life?\", \"answers\": [\"Jimmy\", \"Joel\", \"Scar\", \"Grian\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Which season(s) did Ren NOT participate in?\", \"answers\": [\"Limited, Secret, and Real Life\", \"Secret Life\", \"Limited and Secret Life\", \"Limited Life\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Who was the last green in Third Life?\", \"answers\": [\"Martyn\", \"Scott\", \"Grian\", \"BigB\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Who said this quote: \\\"He hasn't looked at my eyes once, he is looking at my feet!\\\"\", \"answers\": [\"Impulse\", \"Bdubs\", \"Etho\", \"Tango\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who said this quote: \\\"I'll never forget your hole BigB, that thing is huge and dangerous.\\\"\", \"answers\": [\"Scar\", \"Jimmy\", \"Martyn\", \"Grian\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"After a certain player's final death in Third Life, a hoard of zombies showed up at Dogwarts. Who was the player?\", \"answers\": [\"Joel\", \"Cleo\", \"Jimmy\", \"Skizz\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Double Life was the season with the fewest amount of players. How many players participated?\", \"answers\": [\"10 players\", \"12 players\", \"14 players\", \"16 players\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who is the only Life Series winner to still be alive after ending their winning season?\", \"answers\": [\"Martyn\", \"Scott\", \"Cleo\", \"Scar\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Who put a cake in Jimmy's house in Third Life?\", \"answers\": [\"Grian\", \"Martyn\", \"Scott\", \"Joel\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"How did Martyn kill Cleo in Double Life?\", \"answers\": [\"Divorce papers\", \"Rancid vibes\", \"Trust exercise\", \"Bad math\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Who tried to base on top of a Pillager Outpost?\", \"answers\": [\"Cleo, Scott, Pearl, and Martyn\", \"Impulse, Bdubs, Ren, and BigB\", \"Tango, Jimmy, Grian, and Scar\", \"Etho, Joel, Grian, and Scar\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"What was Plan Bubblevate?\", \"answers\": [\"Scott's plan to build a bubblevator elevator to the tower above the Scottage\", \"Joel's plan to kill Scar by building a bubblevator below Magical Mountain\", \"An emergency bubblevator exit from Ren's Shadow Tower into Cleo and BigB's castle\", \"A trap laid by Ren to kill red name players by green names under bubblevators\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Who did Mumbo shove off the Ghast farm in Last Life session 7?\", \"answers\": [\"Grian\", \"Impulse\", \"Joel\", \"Jimmy\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Who made the Team BEST shields?\", \"answers\": [\"Bdubs\", \"Tango\", \"Etho\", \"Skizz\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Which soulmate duo in Double Life made their goal creating distrust between soulmates?\", \"answers\": [\"Martyn and Cleo\", \"Ren and BigB\", \"Etho and Joel\", \"Impulse and Bdubs\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Who subbed in for Pearl during Limited Life?\", \"answers\": [\"Shelby\", \"Lizzie\", \"Gem\", \"False\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Which player was eliminated from Last Life by accidentally blowing himself up?\", \"answers\": [\"Joel\", \"Skizz\", \"Mumbo\", \"Tango\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Which two red names built a massive lava cast over the middle of the Last Life map?\", \"answers\": [\"Cleo and Bdubs\", \"Mumbo and Jimmy\", \"Lizzie and Joel\", \"Grian and Joel\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who named \\\"The Pufferish of Peace\\\" in Third Life?\", \"answers\": [\"Martyn\", \"Scott\", \"Jimmy\", \"Scar\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who was Grian \\\"soulbound\\\" to in session 3 of Secret Life?\", \"answers\": [\"Scar\", \"Martyn\", \"Etho\", \"Joel\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"What animals did Scott and Pearl keep as pets in Last Life?\", \"answers\": [\"Axolotls\", \"Wolves\", \"Sheep\", \"Cows\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"What did Tango name his and Jimmy's Warden in Double Life?\", \"answers\": [\"Rancher's Revenge\", \"Rancher's Wrath\", \"Rancher's Bodyguard\", \"It didn't have a name\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Why did Ren begin wearing a crown in Third Life?\", \"answers\": [\"To show off his MCC win\", \"Got it in a box of cereal\", \"To make Dogwarts into a kingdom\", \"Carried over from Hermitcraft\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"What was the name of Grian's Magma cube?\", \"answers\": [\"Etho's Fridge\", \"Etho's Oven\", \"Etho's Dishwasher\", \"Etho's Microwave\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"What was the color of Skizz's underwear in Limited Life?\", \"answers\": [\"Red\", \"Yellow\", \"Green\", \"White with hearts\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"How was the Double Life Warden killed?\", \"answers\": [\"Bow and arrow\", \"Fishing rod\", \"Sword\", \"Axe\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Who killed the Ender Dragon in Secret Life?\", \"answers\": [\"Scott\", \"Impulse\", \"Pearl\", \"Bdubs\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"BigB has an alternate ego that he uses throughout the series. What is his name?\", \"answers\": [\"Cherry\", \"Berry\", \"Terry\", \"Jerry\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Which one of these hasn't been a feature in Bdubs' skin during the Life Series?\", \"answers\": [\"A golden tooth\", \"A red bandana\", \"A black eye\", \"A missing tooth\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"In Third Life, how did Martyn kill Ren to turn him red?\", \"answers\": [\"With an axe\", \"With TNT\", \"With a sword\", \"Shoved him off a cliff\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Who won \\\"You Bet Your Life\\\" in Last Life?\", \"answers\": [\"Cleo\", \"Scott\", \"Joel\", \"Lizzie\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Which players weren't present for the trapdoor sleepover in Secret Life?\", \"answers\": [\"Martyn and Etho\", \"Etho and Tango\", \"Grian and Etho\", \"Martyn and Grian\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"What did Scar and Pearl use to hurt their soulmates in Double Life?\", \"answers\": [\"Lava\", \"Gravel/sand\", \"Powdered snow\", \"Drowning\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who was referred to as the \\\"Red King\\\" in Third Life?\", \"answers\": [\"Martyn\", \"Scar\", \"Ren\", \"Bdubs\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"When did Gem make her first appearance in the Life Series?\", \"answers\": [\"Wild Life\", \"Secret Life\", \"Limited Life\", \"Real Life\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"What was the name of Pearl's cat in Limited Life?\", \"answers\": [\"Froggy\", \"Tilly\", \"Jumpy\", \"Hoppy\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"What was the cause of Lizzie's final death in Secret Life?\", \"answers\": [\"Drowning\", \"An Enderman\", \"Fell into the void\", \"Fall damage\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Which goat horn type did Jimmy and Tango have in Double Life?\", \"answers\": [\"Ponder\", \"Sing\", \"Dream\", \"Seek\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"How many times did Skizz die in the first episode of Limited Life?\", \"answers\": [\"4 times\", \"3 times\", \"2 times\", \"5 times\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Who was the first to fail a task in Secret Life?\", \"answers\": [\"Mumbo\", \"Jimmy\", \"Skizz\", \"Scar\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"What was the name of Scott and Pearl's base in Last Life?\", \"answers\": [\"Scottagecore\", \"The Mosscottage\", \"Home Sweet Home\", \"The Scottage\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Who opened the End portal in Secret Life?\", \"answers\": [\"Gem\", \"Pearl\", \"Impulse\", \"Bdubs\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Who were the wizards in Last Life?\", \"answers\": [\"Scott and Pearl\", \"Bdubs and Etho\", \"Joel and Lizzie\", \"Scar and Joel\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Who started the zombie curse in Secret Life session 7?\", \"answers\": [\"Cleo\", \"Bdubs\", \"Gem\", \"Martyn\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"What block was Etho's swamp fortress primarily made out of in Third Life?\", \"answers\": [\"Leaves\", \"Spruce\", \"Wool\", \"Dark oak\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who betrayed Cleo in Last Life?\", \"answers\": [\"Scott\", \"Lizzie\", \"BigB\", \"Ren\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who had a wolf pack in Third Life?\", \"answers\": [\"Scar\", \"Grian\", \"Joel\", \"Pearl\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"On what mob did Gem and Pearl ride around killing people together?\", \"answers\": [\"Horse\", \"Skeleton horse\", \"Pig\", \"Camel\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Who was the first ever Boogeyman in the Life Series?\", \"answers\": [\"Etho\", \"Bdubs\", \"Grian\", \"Scott\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Who pranked other players with light grey stained glass in Third Life?\", \"answers\": [\"Etho\", \"Impulse\", \"Tango\", \"Bdubs\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"What was the name of Scar's base in Secret Life?\", \"answers\": [\"Saloon Scar\", \"Scar's Outpost\", \"Trader Scar's\", \"Scar's Emporium\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"In Limited Life, Pearl and BigB decorated their tower with the face of an animal. What was it?\", \"answers\": [\"Frogs\", \"Wolves\", \"Sheep\", \"Cats\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Who built the Ghast farm in Last Life?\", \"answers\": [\"Etho and Grian\", \"Impulse and Tango\", \"Grian and Mumbo\", \"Impulse and Mumbo\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who did Mumbo compete against to have the biggest tower in Secret Life?\", \"answers\": [\"Grian\", \"Gem\", \"Pearl\", \"Joel\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Who was the S in team BEST?\", \"answers\": [\"Scott\", \"Scar\", \"Skizz\", \"Sgrian\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Between Bdubs, Impulse, Tango, and Scott, who was out first during Tag in Secret Life?\", \"answers\": [\"Impulse\", \"Tango\", \"Bdubs\", \"Scott\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Who threw a birthday party in Limited Life?\", \"answers\": [\"Jimmy\", \"Martyn\", \"Joel\", \"Scott\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"What was Lizzie's base in Secret Life?\", \"answers\": [\"Giant peach\", \"Giant watermelon\", \"Giant apple\", \"Giant pumpkin\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Which team blew up Bread Bridge in Limited Life?\", \"answers\": [\"Clockers\", \"Nosey Neighbots\", \"TIES\", \"Mean Gills\"], \"correct_answer_index\": 2}\n" +
"]";

    private static final String DEFAULT_HARD_TRIVIA = "[\n" +
"    {\"question\": \"What was the name of Skizz and Tango's first base in Last Life?\", \"answers\": [\"The Stone Maze\", \"The Roctopus\", \"The Rock Labrynth\", \"The Stonetopus\", \"The Rocksnake\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Which player managed to gain more than 24 hours on their clock during Limited Life?\", \"answers\": [\"Scott\", \"Joel\", \"Cleo\", \"Grian\", \"Bdubs\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Which player came up with the idea to torture their soulmate with powdered snow?\", \"answers\": [\"Scott\", \"Scar\", \"Martyn\", \"Pearl\", \"Cleo\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"How many players did Skizz eliminate in Third Life?\", \"answers\": [\"0\", \"1\", \"2\", \"3\", \"All of them\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who said this quote? \\\"[...] the blood is dripping into me eyes! I can't see, I've been blinded by the violence [...]\\\"\", \"answers\": [\"Skizz\", \"Scar\", \"Bdubs\", \"Ren\", \"Martyn\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Who was this Double Life quote about? \\\"There is something wicked within you.\\\"\", \"answers\": [\"Cleo\", \"Ren\", \"Pearl\", \"Martyn\", \"Joel\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"In Secret Life, Gem, Mumbo, and Impulse were terrorized by which invisible player?\", \"answers\": [\"Lizzie\", \"Etho\", \"Tango\", \"Martyn\", \"Jimmy\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Who had the fewest kills in Limited Life?\", \"answers\": [\"Bdubs\", \"Skizz\", \"Tango\", \"Jimmy\", \"BigB\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who said this quote: \\\"You gain an ally, I gain a foot, pretty good Monday for me.\\\"\", \"answers\": [\"Scar\", \"Ren\", \"Martyn\", \"Etho\", \"Bdubs\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who said this quote: \\\"I wanna hear Mumbo bark.\\\"\", \"answers\": [\"Gem\", \"Jimmy\", \"Scar\", \"Martyn\", \"Skizz\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Who was the second Boogeyman in the first session of Limited Life?\", \"answers\": [\"Scott\", \"Skizz\", \"Martyn\", \"Scar\", \"Bdubs\"], \"correct_answer_index\": 4},\n" +
"    {\"question\": \"Who was the Boogeyman in the third session of Limited Life?\", \"answers\": [\"Etho\", \"Grian\", \"Bdubs\", \"Impulse\", \"Pearl\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Which player had the fewest deaths during Limited Life?\", \"answers\": [\"Bdubs\", \"Tango\", \"BigB\", \"Pearl\", \"Skizz\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Which of these players had the same amount of deaths in Limited Life as Jimmy?\", \"answers\": [\"Skizz\", \"Martyn\", \"Tango\", \"Joel\", \"Scar\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Who were the Blue Sword Boys?\", \"answers\": [\"Tango, Skizz, and Impulse\", \"Martyn, Ren, and Skizz\", \"BigB, Grian, and Martyn\", \"Martyn, Jimmy, and Scott\", \"Martyn, BigB, and Ren\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Which was the only season where Scott did NOT place within the top 5?\", \"answers\": [\"Third Life\", \"Last Life\", \"Double Life\", \"Limited Life\", \"Secret Life\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"What forbidden item did Pearl find in the first session of Double Life?\", \"answers\": [\"Potion of Health\", \"Potion of Regeneration\", \"Golden apple\", \"Totem of Undying\", \"Potion of Strength\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Which two players have not changed their skin for / because of the Life Series? (Not including swapping from a themed skin on another server to a \\\"default\\\" skin)\", \"answers\": [\"Tango and Mumbo\", \"Etho and Tango\", \"Mumbo and Etho\", \"Skizz and Etho\", \"Grian and Mumbo\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Which phrase was NOT on the sign protecting Etho's dark oak tree in Last Life?\", \"answers\": [\"Do Not Touch\", \"Do Not Steal\", \"Do Not Sell\", \"Do Not Burn\", \"Do Not Scar\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Who said this quote: \\\"The Red King dies tonight, fellas!\\\"\", \"answers\": [\"Bdub\", \"Joel\", \"Cleo\", \"Grian\", \"Scar\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Who said this quote: \\\"Call the dogs of war!\\\"\", \"answers\": [\"Joel\", \"Lizzie\", \"Pearl\", \"Scar\", \"Grian\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Which is the only season that Grian did NOT outlive his closest allies?\", \"answers\": [\"Last Life\", \"Double Life\", \"Limited Life\", \"Secret Life\", \"Real Life\"], \"correct_answer_index\": 4},\n" +
"    {\"question\": \"Which of these players had at least ONE kill in Secret Life?\", \"answers\": [\"Lizzie\", \"Tango\", \"Skizz\", \"Impulse\", \"Cleo\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Which two players were tied for most deaths in Limited Life?\", \"answers\": [\"Joel and Scott\", \"Skizz and Pearl\", \"Martyn and Cleo\", \"Jimmy and Etho\", \"Grian and Impulse\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Which two players fought for Lizzie's heart in the first episode of Secret Life?\", \"answers\": [\"Tango and Cleo\", \"Gem and Skizz\", \"Pearl and Joel\", \"Scar and Jimmy\", \"Etho and Grian\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Why was the mansion empty in the first episode of Limited Life?\", \"answers\": [\"The mansion wasn't empty\", \"They disabled all the aggressive mobs\", \"The mobs were bugging\", \"They killed all the Pillagers\", \"The game mode was set to Easy\"], \"correct_answer_index\": 4},\n" +
"    {\"question\": \"In Last Life, Etho gave away papers falsely framing someone as the Boogeyman. Who did he frame?\", \"answers\": [\"Impulse\", \"Martyn\", \"Scar\", \"Pearl\", \"BigB\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Which woodland mansion mob killed Joel and Grian in Limited Life?\", \"answers\": [\"Vex\", \"Evokers\", \"Illager\", \"Vindicators\", \"They didn't die in the mansion\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Which player dropped from 4 lives to 1 in a single session of Last Life?\", \"answers\": [\"Scar\", \"Joel\", \"Pearl\", \"Tango\", \"Jimmy\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Other the Clockers, what Limited Life team was Bdubs involved with?\", \"answers\": [\"Mean Gills\", \"Bad Boys\", \"Team TIES\", \"Nosey Neighbours\", \"He was loyal to the Clockers\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"What was the name of the Ravager that killed Jimmy in Limited Life?\", \"answers\": [\"Grian\", \"Sunday Driver\", \"Bad Boys Big Bad Dog\", \"The Curse\", \"Boogeyman\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"In Third Life, Scar used a clock to convince Bdubs to eliminate Impulse. Where did he get it?\", \"answers\": [\"Found it in a chest\", \"Cleo gave it to him\", \"He crafted it himself\", \"Bdubs dropped it\", \"Impulse dropped it\"], \"correct_answer_index\": 4},\n" +
"    {\"question\": \"Which player had the fewest deaths in Last Life?\", \"answers\": [\"Scott\", \"BigB\", \"Martyn\", \"Etho\", \"Jimmy\"], \"correct_answer_index\": 4},\n" +
"    {\"question\": \"Which Lyrics of \\\"All Star\\\" by Smash Mouth did Joel NOT say as part of a Secret Life task?\", \"answers\": [\"I could use a little fuel myself\", \"So much to do, so much to see\", \"I need to get myself away from this place\", \"I ain’t the sharpest tool in the shed\", \"My world’s on fire, how about yours?\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Who said this quote? \\\"You know, I've always had a thing for red names, bad boys...\\\"\", \"answers\": [\"Martyn\", \"Jimmy\", \"Ren\", \"Lizzie\", \"Scar\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Out of these Third Life quotes, which one was not said by Scar?\", \"answers\": [\"Grian, watch out, we're taking our pants off\", \"Skizz I want all your clothes\", \"I'm naked and come in peace\", \"Yes, we are pants swapers\", \"Everyone always worries about me being pantsless\"], \"correct_answer_index\": 4},\n" +
"    {\"question\": \"In Secret Life, who was the only player to show up to Lizzie's slumber party?\", \"answers\": [\"Pearl\", \"Cleo\", \"No one showed up\", \"Joel\", \"Gem\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Who was Grian's \\\"Secret Soulmate\\\" in Double Life?\", \"answers\": [\"Ren\", \"Martyn\", \"Pearl\", \"BigB\", \"Etho\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"What pet did Mumbo try to drown in Secret Life?\", \"answers\": [\"Grian's Magma Cube\", \"Scar's camel\", \"Etho's Warden\", \"Bdubs' horse\", \"Pearl's dog\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Who burned down Jimmy and Scott's wall in Third Life?\", \"answers\": [\"Martyn\", \"Joel\", \"Jimmy\", \"Scar\", \"Tango\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"What was Mumbo's ultimate weapon to stop a red name in Secret Life?\", \"answers\": [\"A giant redstone machine\", \"Act like a red name\", \"Run, cry, and scream, in that specific order\", \"Fence post\", \"Ask for mercy\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"Which player appeared the tallest in the April Fools episode Real Life?\", \"answers\": [\"BigB\", \"Pearl\", \"Jimmy\", \"Skizz\", \"Impulse\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Who killed Lizzie while she was invisible on a skeleton horse in Secret Life?\", \"answers\": [\"Jimmy\", \"A mob killed her\", \"She killed herself\", \"Joel\", \"Martyn\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"When Scar turned red in Third Life, he gave Grian two types of flowers as a peace offering. What were these flowers?\", \"answers\": [\"Rose bushes and alliums\", \"Lilies of the valley and tulips\", \"Sunflowers and oxeye daisies\", \"Dandelions and blue orchids\", \"Lilacs and poppies\"], \"correct_answer_index\": 4},\n" +
"    {\"question\": \"Which player started the first ever Raid in the Life Series?\", \"answers\": [\"Martyn\", \"Grian\", \"Impulse\", \"Tango\", \"Scar\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"In every single season he's participated in so far, Mumbo has died right after which player?\", \"answers\": [\"Skizz\", \"Lizzie\", \"Jimmy\", \"Grian\", \"Tango\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Which player lost all of their lives in one session of Third Life?\", \"answers\": [\"BigB\", \"Joel\", \"Tango\", \"Grian\", \"Scar\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Which of these did Mumbo use to get a Boogey kill in Last Life?\", \"answers\": [\"Bow and arrow\", \"End Crystal\", \"Fire charge\", \"Sword\", \"TNT\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"Which of these was NOT a house of Dogwarts?\", \"answers\": [\"Cluckle Cluck\", \"Carrotyn\", \"Crimson Wart\", \"Enderport\", \"Rose Thorn\"], \"correct_answer_index\": 4},\n" +
"    {\"question\": \"Who said this quote? \\\"Do me a favor? Die for me.\\\"\", \"answers\": [\"Cleo\", \"Scar\", \"Pearl\", \"Joel\", \"Ren\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Which player makes it their goal to go to the Nether in the first session of every season?\", \"answers\": [\"Scott\", \"Martyn\", \"Pearl\", \"Etho\", \"Bdubs' horse\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"What was the name of Scar's bee in Third Life?\", \"answers\": [\"Mr. Pepperoni\", \"Mr. Bee\", \"Mr. Bubbles\", \"Mr. Yoda\", \"Mr. Balloon\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"What did Ren do with the Wither Star in Last Life?\", \"answers\": [\"Crafted a beacon\", \"Forgot it in a random chest\", \"Gave it to an ally\", \"Used it as a good-luck charm\", \"Hung it up on a wall in his base\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"How did Gem lose her eye in Secret Life?\", \"answers\": [\"She lost it to the End portal\", \"There was no lore, she just did it for fun\", \"A skeleton shot her\", \"The Boogeyman curse\", \"She didn't lose an eye\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"In Double Life, Tango brought a Warden to the surface to kill two specific players. Who were they?\", \"answers\": [\"Scott and Pearl\", \"Martyn and Cleo\", \"Scar and Grian\", \"Impulse and Bdubs\", \"Etho and Joel\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"What did Martyn hand over to Pearl when she asked for Tilly in Limited Life?\", \"answers\": [\"Her ashes\", \"Her bones\", \"A new wolf\", \"A cat\", \"Rotten flesh\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"In Last Life, Lizzie had 3 wolves. Identify one of the two incorrect names here.\", \"answers\": [\"Troll\", \"Ogre\", \"Dragon\", \"Taxes\", \"Gas-Prices\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Who stole Scar's llama \\\"Pizza\\\" in Third Life?\", \"answers\": [\"Ren\", \"Martyn\", \"Grian\", \"Cleo\", \"Jimmy\"], \"correct_answer_index\": 3},\n" +
"    {\"question\": \"What season did Skizz NOT participate in?\", \"answers\": [\"Last Life\", \"Double Life\", \"He was there the whole time\", \"Secret Life\", \"Limited Life\"], \"correct_answer_index\": 1},\n" +
"    {\"question\": \"In Third Life, Impulse showed off a trick to find which ore?\", \"answers\": [\"Lapis\", \"Emeralds\", \"Iron\", \"Ancient debris\", \"Diamonds\"], \"correct_answer_index\": 4},\n" +
"    {\"question\": \"In Limited Life, Jimmy had a frog which was killed by Pearl. What was its name?\", \"answers\": [\"Froggy D Luffy\", \"Froggy\", \"Leonardo da Vinci\", \"Bubbles The Frog\", \"Judge Judy And Executioner\"], \"correct_answer_index\": 4},\n" +
"    {\"question\": \"What base did Cleo burn down in Last Life session 5 as an act of revenge?\", \"answers\": [\"The Fairy Fort\", \"Magical Mountain\", \"The Southlands\", \"Snow Fort\", \"Scottage\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"Which soulmate pair invited a hoard of zombies to the pool party in Double Life?\", \"answers\": [\"Ren and BigB\", \"Etho and Joel\", \"Tango and Jimmy\", \"Impulse and Bdubs\", \"Scar and Grian\"], \"correct_answer_index\": 0},\n" +
"    {\"question\": \"What was the name of Skizz's dog in Last Life?\", \"answers\": [\"Kevin Mad-Eye Malone Jimmy Refrigerator Bubbles Dugan\", \"Kevin Malone Jimmy Bubbles Refrigerator Mad-Eye Dugan\", \"Kevin Bubbles Malone Refrigerator Jimmy Mad-Eye Dugan\", \"Kevin Refrigerator Malone Bubbles Mad-Eye Jimmy Dugan\", \"I ain't reading all that\"], \"correct_answer_index\": 2},\n" +
"    {\"question\": \"Why did Cleo turn into a human in Secret Life?\", \"answers\": [\"She didn't turn into a human\", \"She was given a weakness potion and a golden apple\", \"Someone subbed in for her\", \"She survived the Zombie Apocalypse\", \"It was for her task\"], \"correct_answer_index\": 3}\n" +
    "{ \"question\": \"What was the name of Grian's Iron Golem in Wild Life?\", \"answers\": [\"Bloop\", \"Bleep\", \"Boop\", \"Beep\", \"Beep boop\"], \"correct_answer_index\": 0 },\n" +
    "{ \"question\": \"Which feature from Cleo's skin did not appear on her snail in Wild Life?\", \"answers\": [\"Blue flowers\", \"Orange hair\", \"Black and white stripes\", \"Visible ribs\", \"Green eyes\"], \"correct_answer_index\": 3 },\n" +
    "{ \"question\": \"What visual effect did Cleo see when she used her super power in Wild Life?\", \"answers\": [\"Lightning strikes\", \"Her screen shook\", \"Darkness\", \"Text appeared on the screen\", \"The zombies glowed\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"Who did Lizzie kill after rigging Etho's house with TNT in Wild Life?\", \"answers\": [\"Tango\", \"Bdubs\", \"Martyn\", \"Etho\", \"Skizz\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"If I had a nickel for every time Jimmy wasn't first out in a full season of the Life Series, how many nickels would I have?\", \"answers\": [\"Zero, which really means nothing\", \"One, which isn't a lot but at least it's something\", \"Two, which isn't a lot but it's weird it happened twice\", \"Three, which isn't a lot but is a strange amount of nickels to have\", \"Four, which isn't a lot but at that point you could just have two dimes\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"What song did Ren sing in session one of Wild Life?\", \"answers\": [\"Let It Be\", \"Lua\", \"Creep\", \"House of the Rising Sun\", \"Somebody Like You\"], \"correct_answer_index\": 3 },\n" +
    "{ \"question\": \"What was the name of Skizz's snail in Wild Life?\", \"answers\": [\"SnailMan\", \"SkizzleShell\", \"Skail\", \"SkizzleSnail\", \"ShellMan\"], \"correct_answer_index\": 0 },\n" +
    "{ \"question\": \"Which player was the first to land a mace kill?\", \"answers\": [\"Scar\", \"Mumbo\", \"Skizz\", \"Tango\", \"Pearl\"], \"correct_answer_index\": 1 },\n" +
    "{ \"question\": \"What was the name of Pearl's dog in Wild Life?\", \"answers\": [\"Nilly\", \"Billy\", \"Milly\", \"Dilly\", \"Tilly\"], \"correct_answer_index\": 1 },\n" +
    "{ \"question\": \"Who was Jimmy's first kill?\", \"answers\": [\"Grian\", \"Lizzie\", \"Ren\", \"Skizz\", \"Joel\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"What alliance was Etho part in Wild Life?\", \"answers\": [\"All of them\", \"The 4Gs\", \"The Tuff Guys\", \"The Bamboozlers\", \"The Family\"], \"correct_answer_index\": 0 },\n" +
    "{ \"question\": \"Who made the 4G's tower into a birthday cake?\", \"answers\": [\"Skizz\", \"Jimmy\", \"Scott\", \"Pearl\", \"BigB\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"Who is Joel roleplaying as in Wild Life?\", \"answers\": [\"Roman Paerce\", \"Tej Parker\", \"Vin Diesel\", \"Mia Toretto\", \"Dominic Toretto\"], \"correct_answer_index\": 4 },\n" +
    "{ \"question\": \"What did Mumbo say about Joel's car in Wild Life?\", \"answers\": [\"\\\"That's the worst thing I've ever seen\\\"\", \"\\\"Grian? Is that car big?\\\"\", \"\\\"I'm quite chuffed with it\\\"\", \"\\\"That looks awesome\\\"\", \"\\\"It's quite cinematic, really\\\"\"], \"correct_answer_index\": 0 },\n" +
    "{ \"question\": \"What was the name of Martyn's snail in Wild Life?\", \"answers\": [\"Snartyn\", \"InTheLittleSlime\", \"Mail\", \"InTheLittleShell\", \"InTheLittleSnail\"], \"correct_answer_index\": 3 },\n" +
    "{ \"question\": \"Who was the first player to be killed by VHSnail in Wild Life?\", \"answers\": [\"Mumbo\", \"Jimmy\", \"Skizz\", \"Scar\", \"Lizzie\"], \"correct_answer_index\": 3 },\n" +
    "{ \"question\": \"Who gave Tango the mace he used to kill Joel in Wild Life?\", \"answers\": [\"Jimmy\", \"He got it himself\", \"Skizz\", \"Etho\", \"Bdubs\"], \"correct_answer_index\": 4 },\n" +
    "{ \"question\": \"Which of these players became giant after answering a question incorrectly in Wild Life?\", \"answers\": [\"Lizzie\", \"BigB\", \"Jimmy\", \"Bdubs\", \"Joel\"], \"correct_answer_index\": 3 },\n" +
    "{ \"question\": \"What does Grian call the Spanners' TNT minecart launcher in Wild Life?\", \"answers\": [\"The Spider's Nest\", \"It doesn't have a name\", \"Murder Machine\", \"Death Loop\", \"The Launchpad\"], \"correct_answer_index\": 0 },\n" +
    "{ \"question\": \"In Wild Life, what question does Pearl ask Gem almost every session?\", \"answers\": [\"\\\"Do you have any pickles?\\\"\", \"\\\"Can we be friends yet?\\\"\", \"\\\"Need help turning red today?\\\"\", \"\\\"How much do you hate me today?\\\"\", \"\\\"Can I kill you today?\\\"\"], \"correct_answer_index\": 3 },\n" +
    "{ \"question\": \"When building their bases, Bdubs required the Tuff Guys to adhere to what?\", \"answers\": [\"A specific distance between each base\", \"Bases facing the same direction\", \"Specific base materials\", \"A specific color theme\", \"The golden ratio\"], \"correct_answer_index\": 4 },\n" +
    "{ \"question\": \"Before it blew up, which group was lowest on Scar's reputation board?\", \"answers\": [\"The Family\", \"The 4Gs\", \"The Spanners\", \"The Tuff Guys\", \"Ren Mounders/InTheLittleLake\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"Why did Impulse use a Totem of Undying?\", \"answers\": [\"Jimmy punched him off a tower\", \"He fell into lava\", \"Mumbo blew him up\", \"He lost a fight against Joel\", \"Scar killed him\"], \"correct_answer_index\": 0 },\n" +
    "{ \"question\": \"What was the second Wild Card in Wild Life?\", \"answers\": [\"Snails\", \"Eating items\", \"Size change\", \"Time change\", \"Trivia Bot\"], \"correct_answer_index\": 1 },\n" +
    "{ \"question\": \"What did Etho say at Skizz's funeral in Wild Life?\", \"answers\": [\"\\\"I'll miss you buddy\\\"\", \"\\\"We didn't talk that much\\\"\", \"\\\"He was the best Skizz to ever skizz\\\"\", \"Skill issue I guess\", \"\\\"I wish you were better at this game\\\"\"], \"correct_answer_index\": 4 },\n" +
    "{ \"question\": \"Who found the Trial Chambers in Wild Life?\", \"answers\": [\"Joel\", \"Grian\", \"Scott\", \"Gem\", \"Etho\"], \"correct_answer_index\": 0 },\n" +
    "{ \"question\": \"Right before Mumbo permadied in Wild Life, he was going to aim his TNT minecart to kill which player?\", \"answers\": [\"Scar\", \"Lizzie\", \"Jimmy\", \"Joel\", \"Gem\"], \"correct_answer_index\": 4 },\n" +
    "{ \"question\": \"Who is the Bamboozler's team mascot?\", \"answers\": [\"Jimmy\", \"Scar\", \"Their pet parrot\", \"Lizzie\", \"Their pet wolf\"], \"correct_answer_index\": 0 },\n" +
    "{ \"question\": \"Who was the first player to have a custom skin specifically for Wild Life?\", \"answers\": [\"Scar\", \"Scott\", \"Joel\", \"Gem\", \"BigB\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"Apart from blindness, what happened to Lizzie when she activated her super powers in Wild Life?\", \"answers\": [\"She got regeneration\", \"She turned invisible\", \"She teleported\", \"She saw everyone with the glowing effect\", \"Nothing else\"], \"correct_answer_index\": 1 },\n" +
    "{ \"question\": \"What did Etho name the Warden in Wild Life?\", \"answers\": [\"He didn't name it\", \"Etho's Dishwasher\", \"Winton 2\", \"Bdubs\", \"Goober\"], \"correct_answer_index\": 4 },\n" +
    "{ \"question\": \"How many Creakings could BigB spawn at one time with his super power in Wild Life?\", \"answers\": [\"1\", \"2\", \"3\", \"4\", \"5\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"Who was the last player with a dark green name in Wild Life?\", \"answers\": [\"Etho\", \"Lizzie\", \"Gem\", \"Joel\", \"Scott\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"Out of these Wild Life quotes, which one was NOT said by Scar?\", \"answers\": [\"\\\"Let's make some torches and start stripping\\\"\", \"\\\"So I can only bang one person with this?\\\"\", \"\\\"BAM doesn't happen to us, we BAM them!\\\"\", \"\\\"Did you BAM him?\\\"\", \"\\\"I'm so sorry I promise not to BAM you ever again\\\"\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"What valuable item did Scott and Jimmy decide to eat together in Wild Life?\", \"answers\": [\"Emeralds\", \"Diamonds swords\", \"Creeper Eggs\", \"Golden Apple\", \"Diamonds\"], \"correct_answer_index\": 4 },\n" +
    "{ \"question\": \"In session five of Wild Life, Cleo burned down the home of which player?\", \"answers\": [\"Ren\", \"Tango\", \"Scar\", \"Martyn\", \"Gem\"], \"correct_answer_index\": 4 },\n" +
    "{ \"question\": \"Who stole the 4G's cows in the first session of Wild Life?\", \"answers\": [\"Mumbo and Skizz\", \"Grian and Martyn\", \"BigB and Jimmy\", \"Tango and Etho\", \"Gem and Joel\"], \"correct_answer_index\": 1 },\n" +
    "{ \"question\": \"Who eliminated Skizz in Wild Life?\", \"answers\": [\"Etho\", \"Tango\", \"Himself\", \"Grian\", \"Bdubs\"], \"correct_answer_index\": 1 },\n" +
    "{ \"question\": \"Who got the first kill in Wild Life?\", \"answers\": [\"Tango\", \"Pearl\", \"Skizz\", \"Mumbo\", \"Scar\"], \"correct_answer_index\": 0 },\n" +
    "{ \"question\": \"Who was the first person to die to their snail in Wild Life?\", \"answers\": [\"Scar\", \"Mumbo\", \"Jimmy\", \"Skizz\", \"BigB\"], \"correct_answer_index\": 4 },\n" +
    "{ \"question\": \"What was Ren's super power in Wild Life?\", \"answers\": [\"Double jumping\", \"Flight\", \"Shapeshifting\", \"Water breathing\", \"Super hearing\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"Who did Pearl gift the Totem of Undying to in Wild Life?\", \"answers\": [\"She kept it\", \"Cleo\", \"Impulse\", \"Scott\", \"BigB\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"What did Bdubs and Etho fight over in session one of Wild Life?\", \"answers\": [\"A horse\", \"A saddle\", \"Iron\", \"A music disc\", \"Diamonds\"], \"correct_answer_index\": 0 },\n" +
    "{ \"question\": \"Which of these players never died to their snail in Wild Life?\", \"answers\": [\"Bdubs\", \"Scott\", \"Etho\", \"Impulse\", \"Ren\"], \"correct_answer_index\": 1 },\n" +
    "{ \"question\": \"What was Scar's dog named in Wild Life?\", \"answers\": [\"It had no name\", \"Shears\", \"Putter\", \"Scarf\", \"Wolfy\"], \"correct_answer_index\": 1 },\n" +
    "{ \"question\": \"What was the name of BigB's snail in Wild Life?\", \"answers\": [\"Bigbstropod\", \"BigbSlime\", \"Bail\", \"BigbShell\", \"BigbSnail\"], \"correct_answer_index\": 3 },\n" +
    "{ \"question\": \"Who made Scar's snail invisible in session three of Wild Life?\", \"answers\": [\"Grian\", \"Etho\", \"Ren\", \"Joel\", \"Pearl\"], \"correct_answer_index\": 2 },\n" +
    "{ \"question\": \"What flower did Gem give to Etho in Wild Life?\", \"answers\": [\"Lily of the Valley\", \"Allium\", \"Dandelion\", \"Oxeye Daisy\", \"Poppy\"], \"correct_answer_index\": 1 },\n" +
    "{ \"question\": \"Whose snail won the Great Snail Race to the top of BAM mountain in Wild Life?\", \"answers\": [\"BigB\", \"Impulse\", \"Cleo\", \"Pearl\", \"Scott\"], \"correct_answer_index\": 1 },\n" +
    "{ \"question\": \"Who died first in Wild Life?\", \"answers\": [\"Pearl\", \"Skizz\", \"Lizzie\", \"Jimmy\", \"Scar\"], \"correct_answer_index\": 0 }\n" +
"];";