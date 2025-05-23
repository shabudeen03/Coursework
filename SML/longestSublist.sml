fun member(x, list) = 
	if list = nil 
		then false 
	else if x = hd(list) 
		then true 
	else member(x, tl(list));

fun longestSublistHelper(list, sublist) = 
	if list = nil 
		then sublist 
	else if member(hd(list), sublist) 
		then longestSublistHelper(tl(list), sublist) 
	else 
		longestSublistHelper(tl(list), hd(list)::sublist);

fun length(list) = 
	if list = nil 
		then 0 
	else 
		1 + length(tl(list));

fun longestSublist(list) = 
	if list = nil 
		then 0 
	else 
		length(longestSublistHelper(list, []));