fun minMaxHelper(min, max, list) = 
	if list = nil 
		then (min, max) 
	else if hd(list) < min 
		then minMaxHelper(hd(list), max, tl(list)) 
	else if hd(list) > max 
		then minMaxHelper(min, hd(list), tl(list)) 
	else 
		minMaxHelper(min, max, tl(list));

fun minMax(list) = 
	if list = nil 
		then (0, 0) 
	else 
		minMaxHelper(hd(list), hd(list), tl(list));