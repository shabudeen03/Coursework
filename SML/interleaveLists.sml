fun min(N1,N2) = 
	if N1<N2 
		then N1 
	else 
		N2;


fun getN(L,M) = 
	if M < 1 orelse L=[] 
		then []
  	else 
		hd(L)::getN(tl(L),M-1);


fun removeN(L,M) = 
	if M < 1 
		then L
  	else if L=[] 
		then []
  	else 
		removeN(tl(L), M - 1);


fun howMany(L,Stride) = 
	if L<Stride 
		then L
  	else 
		Stride;


fun interleaveLists(L1, L2, Stride, MaxElements) =
 	if MaxElements<1 
		then []
  	else if L1=[] andalso L2=[] 
		then []
  	else 
		getN(L1,min(Stride, MaxElements)) @ interleaveLists(L2, removeN(L1, Stride), Stride, MaxElements - howMany(length(L1), Stride));