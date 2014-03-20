<?php
	//get new and old votes in two strings
	$oldVotes = file_get_contents("answers.txt");
	$f = fopen("answers.txt", "w+");

	$answer = $_GET['answer'];
	$oldAbcd = explode(" ", $oldVotes); //the old ones 
	echo $answer;
        
	if("a" == $answer){
		$oldAbcd[0] += 1;
	}
	else if("b" == $answer){
		$oldAbcd[1] += 1;
	}
	else if("c" == $answer){
		$oldAbcd[2] += 1;
	}	
	else if("d" == $answer){
		$oldAbcd[3] += 1;
	}
	else if("e" == $answer){
		$oldAbcd[4] += 1;
	}   
	else{
		$oldAbcd[0] = 0;
		$oldAbcd[1] = 0;
		$oldAbcd[2] = 0;
		$oldAbcd[3] = 0;
		$oldAbcd[4] = 0;
	}
	 
	$string = "{$oldAbcd[0]} {$oldAbcd[1]} {$oldAbcd[2]} {$oldAbcd[3]} {$oldAbcd[4]}"; 
	fwrite($f, $string);
        //fwrite($f, "10 8 7 3 4");
	fclose($f);
?>
