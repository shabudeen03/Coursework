fun groupdupesHelper(list, sublist, sublists) =
	if list = nil 
		then sublists @ [sublist]
	else if sublist = nil 
		then groupdupesHelper(tl(list), hd(list)::sublist, sublists)
	else if hd(list) = hd(sublist) 
		then groupdupesHelper(tl(list), hd(list)::sublist, sublists)
	else 
		groupdupesHelper(tl(list), hd(list)::[], sublists @ [sublist]);

fun groupdupes(list) = 
	if list = nil 
		then nil 
	else 
		groupdupesHelper(list, [], []);