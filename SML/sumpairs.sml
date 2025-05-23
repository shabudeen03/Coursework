fun sumpairsHelper(list, newList) =
	if list = nil 
		then newList
	else if tl(list) = nil 
		then newList @ [hd(list)]
	else 
		sumpairsHelper(tl(tl(list)), newList @ [(hd(list) + hd(tl(list)))]);

fun sumpairs(list) = 
	if list = nil 
		then nil 
	else 
		sumpairsHelper(list, []);