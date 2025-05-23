fun prime_helper(n, p) =
	if p = 1
		then true
	else if n mod p = 0 
		then false
	else 
		prime_helper(n, p - 1);

fun prime(n) = 
	if n < 2
		then false
	else 
		prime_helper(n, n div 2);