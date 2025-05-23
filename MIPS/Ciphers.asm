.text
null_cipher_sf:
	li $v0, 0 # num of characters read into plaintext thus far
	li $t0, 0 #i: which index from indices I am reading
	li $t6, 32 #Space character
	li $t9, 0 #0 Characters were read
	move $t5, $a0 #plaintext copy address
	
	readText:
		bge $t0, $a3, stopReadingText #All indices as needed were read, stop
		
		sll $t2, $t0, 2 #Adjust since each item is 4 bytes, multiply by 4
		add $t2, $t2, $a2 #Is address of indices[i]
		lw $t1, 0($t2)  #indices[i] value
		li $t8, 0   #word index <= indices[i]
		
		readWord:
			beq $t8, $t1, nextWord #Is not pointing to index after the element needed from the word
			
			lb $t7, 0($a1)  #Ciphertext[wordIndex] value
			addi $a1, $a1, 1
			addi $t8, $t8, 1
			j readWord
		
		nextWord: #Skip the ciphertext to the next word
			lb $t4, 0($a1) #t4 - ciphertext[index]
			beq $t4, $zero, lastWord 
			beq $t4, $t6, increment #Now at space character, now need to just adjust register and repeat for next word if any
			
			addi $a1, $a1, 1 #Move to next index otherwise
			j nextWord
						
		increment:
			addi $a1, $a1, 1 #Now at next word
			beq $t8, $t9, skip #if 0 characters were read, skip to next word
			
			sb $t7, 0($t5) #Otherwise, store the cipher text character from the word in plaintext
			addi $t5, $t5, 1 #Update plaintext index
			addi $v0, $v0, 1 # Increment # characters read into plaintext
			
		skip:
		addi $t0, $t0, 1 # Increment Indices index		
		j readText	
		
	lastWord:
		beq $t8, $t9, stopReadingText
		
		sb $t7, 0($t5)
		addi $t5, $t5, 1
		addi $v0, $v0, 1
			
	stopReadingText: #Add null character after the last character read into plaintext
		li $t7, 0 #Null character
		sb $t7, 0($t5) #Will be pointing at next character which should be null character
    jr $ra

transposition_cipher_sf:
	mul $t1, $a2, $a3 #t1 - nr * nc
	li $t2, 0 #t2 - i (0 to nc - 1) Cus transposed, switch rows and columns
	move $t6, $a1 #t6 - ciphertext copy
	move $t7, $a0 #t7 - plaintext copy
	
	restoreOrder:
		beq $t0, $t1, dropStars # cipher index > ciphertext length, dip
		beq $t2, $a3, dropStars #Just to be safe: i > nc, dip
		
		li $t3, 0 #j = 0 to nr - 1
		readRow:
			beq $t3, $a2, nextRow #j > nr, dip
			
			mul $t9, $t3, $a3 #For plaintext idx, i * nc
			add $t4, $t9, $t2 #plaintext idx given i and j, i(nc + j			
			lb $t5, 0($t6) #Byte from ciphertext[index]
			move $t8, $t7 #Second copy of plaintext for storing byte
			add $t8, $t7, $t4  #Correct index in plaintext
			sb $t5, 0($t8) #Store ciphertext at correct spot in plaintext
			
			addi $t3, $t3, 1 #j++
			addi $t6, $t6, 1 #Increment ciphertext to next byte
			j readRow
			
		nextRow:
			addi $t2, $t2, 1 # i++
		j restoreOrder
	
	dropStars: #Now increment plaintext copy to first occurance of * or until nr*nc index is reach		
		li $t9, 42 #Star character
		li $t0, 0 #Now to be used as index for plaintext until it reaches nr*nc or * is reached
		goToLastIndex:
			lb $t8, 0($t7) #Should be character from plaintext at index specified in t0
			beq $t8, $t9, lastIndexFound #t8 - plaintext copy for storing byte
			beq $t0, $t1, lastIndexFound #nr*nc index reached
			
			addi $t7, $t7, 1 #Point to next character in plaintext
			addi $t0, $t0, 1 #Increment index for plaintext
			j goToLastIndex
			
		lastIndexFound:
			li $t9, 0 #Null terminator
			sb $t9, 0($t7) #Should be at the appropriate index at the end of decoded msg

    jr $ra

decrypt_sf:	
	#calculate length of mediary plaintext
	li $t0, 0 #This will be # of asterisks in ciphertext
	li $t1, 0 #Index to go through the cipher text in a1
	li $t4, '*' #This is the asterisk, each time we find this, I increment count of t0
	mul $t5, $a2, $a3 #Length of ciphertext in t5
	addi $t5, $t5, 1 #Add to consider null terminator
	
	findStars: #Find length of mediary plaintext
		beq $t1, $t5, findMediaryLength
		
		add $t2, $t1, $a1 #t2 - address of byte of element at index defined in t1
		lb $t3, 0($t2) #t3 - byte at index in t1
		beq $t3, $t4, starFound
		
		starFound:
			addi $t0, $t0, 1
			
		nextIndex:
		addi $t1, $t1, 1
	
	findMediaryLength:
		sub $t6, $t5, $t0 #t6 - actual length of mediary plain text
		addi $t6, $t6, 1 #Add 1 for null terminator
		
	addi $sp, $sp, -12 #For ra and original plaintext
	sw $ra, 0($sp) # ra on stack
	sw $a0, 4($sp) # address of original plaintext array on stack
	sw $s0, 8($sp) # Store the s register so i can modify it
	
	move $s0, $t6 # I will keep # bytes in s register
	li $t0, -1 #Stack is allocated with negative value
	mul $t6, $t6, $t0 
	add $sp, $sp, $t6 #Allocated memory on stack for mediary plaintext
	move $a0, $sp #Set mediary plaintext to this address in stack
	jal transposition_cipher_sf
	
	move $a1, $a0 #Mediary plaintext is now ciphertext in the null cipher
	add $t0, $s0, $a0 #Offset for null cipher to get a0, a2, a3
	lw $a0, 4($t0) 
	lw $a3, 12($t0)
	lw $a2, 16($t0)
	jal null_cipher_sf
	
	add $sp, $sp, $s0 #free stack from mediary plaintext
	lw $s0, 8($sp) #the preserved s register
	lw $ra, 0($sp) #The original ra
	addi $sp, $sp, 12 #free stack from a0, ra, s0
	jr $ra