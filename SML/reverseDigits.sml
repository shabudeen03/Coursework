fun reverseDigits_helper(N, P) = 
	if N < 10 
		then P * 10 + N 
	else 
		reverseDigits_helper(N div 10, P * 10 + N mod 10);

fun reverseDigits(N) = 
	if N < 0
		then ~(reverseDigits_helper(~N, 0))
	else 	
		reverseDigits_helper(N, 0);