fun member(X, Y) = 
	if Y = nil 
		then false 
	else if X = hd(Y) 
		then true 
	else 
		member(X, tl(Y));

fun union(x, y) = 
	if x = nil 
		then y 
	else if y = nil 
		then x 
	else if member(hd(x), y) 
		then union(tl(x), y) 
	else 
		hd(x)::union(tl(x), y);

fun intersection(x, y) = 
	if x = nil orelse y = nil 
		then nil 
	else if member(hd(x), y) 
		then hd(x)::intersection(tl(x), y) 
	else 
		intersection(tl(x), y);

fun subset(x, y) = 
	if x = nil andalso y = nil 
		then true 
	else if y = nil 
		then false 
	else if x = nil 
		then true 
	else if member(hd(x), y) 
		then subset(tl(x), y) 
	else 
		false;

fun minus(x, y) = 
	if x = nil 
		then nil 
	else if y = nil 
		then x 
	else if member(hd(x), y) 
		then minus(tl(x), y) 
	else 
		hd(x)::minus(tl(x), y);

fun product_help(x, y) = 
	if y = nil 
		then nil 
	else 
		(x, hd(y))::product_help(x, tl(y));

fun product(x, y) = 
	if x = nil 
		then nil 
	else 
		union(product_help(hd(x), y), product(tl(x), y));