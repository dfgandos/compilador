section .data ; Sessão de dados
M: ; Rótulo para demarcar o
; início da sessão de dados
	resb 0x10000 ; Reserva de temporários
; ***Definições de variáveis e constantes
section .text ; Sessão de código
global _start ; Ponto inicial do programa
_start: ; Início do programa
; ***Comandos
section .data
	dd 1 ; Atribuição INTEGER
section .text ; Sessão de código
section .data
	resb 256 ; Declaração String
section .text ; Sessão de código
section .data
	dd 12 ; Atribuição INTEGER
section .text ; Sessao de codigo
section .data
	db "Digite seu nome: " ; Declaração da string
	db 0 ; Declaração do fim da string
section .text
; Impressão String
; Calcula o tamanho da string
	mov RSI, M+0x10108 ; Move o endereço da string para o registrador fonte (source)
	mov RDX, RSI ; Recupera o endereço inicial da string
Rot0: ; Loop para o calculo do tamanho
	mov AL, [RDX] ; Le o caractere atual
	add RDX, 1 ; Incrementa o ponteiro da string
	cmp AL, 0 ; Verifica se o caractere lido eh o byte 0 (fim de string)
	jne Rot0 ; Caso nao seja, continua o loop

	sub RDX, RSI ; Subtrai o endereco inicial da string pelo endereço final, o registrador RDX sera utilizado pela chamada de sistema como o tamanho da string
	sub RDX, 1 ; Desconsidera o byte nulo ao final da string

; Escreve a string para a saída padrão
	mov RSI, M+0x10108 ; Move o endereço da string para o registrador fonte (source)
	mov RAX, 1 ; Chamada de escrita
	mov RDI, 1 ; Escrever para a saida padrao
	syscall ; Chama o kernel
; Leitura

	mov RSI, M+0x10004 ; Salva o endereço do buffer
	mov RDX, 256 ; Tamanho do buffer
	mov RAX, 0 ; Chamada para leitura
	mov RDI, 0 ; Leitura do teclado
	syscall

; Leitura string
	add RAX, RSI
	sub RAX, 1
Rot1:
	mov DL, [RSI] ; Move caractere para DL
	cmp DL, 0Ah ; Compara com quebra de linha
	je Rot2 ; Se for quebra de linha salta
	cmp RSI, RAX
	je Rot2
	add RSI, 1 ; Incrementa endereço da String
	jmp Rot1

Rot2:
	mov DL, 0 ; Substitui quebra de linha por fim de string
	mov [RSI], DL ; Move fim de string para o identificador
section .data
	resd 1 ; Declaração real 
section .text ; Sessão de código
; Inicio while
Rot3:
	mov EAX, [M+0x10000] ; Moveu a EXP1(int) para EAX
	mov EBX, [M+0x10104] ; Moveu a EXP2(int) para EBX
	cmp EAX, EBX ; Realiza a comparacao do conteudo de EAX com EBX
	jle Rot5
	mov EAX, 0 ; Caso seja falso
	jmp Rot6
Rot5:
	mov EAX, 1 ; Caso seja verdadeiro
Rot6:
	mov [M+0x0], EAX ; Coloca no temporario se eh true ou false
; Fim exp
	mov EAX, [M+0x0] ; Carrega para EAX o EXP
	cmp EAX, 0 ; Compara se o resultado da exp é falso
	je Rot4

; Inicio atribuicao: percentual
; Inicio conversao para real
	mov RAX, 0 ; Zera o RAX
	mov RAX, [M+0x10000] ; Carrega o valor para a RAX
	subss XMM0, XMM0 ; Zera o XMM0
	cvtsi2ss XMM0, RAX ; Converte inteiro para real
	movss [M+0x0], XMM0 ; Cerrerga para temporario o valor convertido
; Fim conversao para real
; Multiplicação ou Divisão de termos
	movss XMM0, [M+0x0] ; Moveu T1 para XMM0
	mov EAX, [M+0x10104] ; Moveu a T2 para RAX
	cvtsi2ss XMM1, EAX ; Converteu o numero de EAX em real
	divss XMM0, XMM1 ; Realiza a divisão do conteudo de XMM0 com XMM1
	movss [M+0x4], XMM0 ; Coloca no temporario o resultado da multiplicação ou divisão
; Atribuicao float em inteiro
	movss XMM0, [M+0x4] ; Moveu valor float para XMM0
	movss [M+0x1011a], XMM0 ; Coloca no identificador o valor float
; Impressão REAL
	movss XMM0, [M+0x1011a] ; Real a ser convertido
	mov RSI, M+0x0 ; Endereço Temporário
	mov RCX, 0 ; Contador pilha
	mov RDI, 6 ; Precisão 6 casa compartilhadas
	mov RBX, 10 ; Divisor
	cvtsi2ss XMM2, RBX ; Divisor real
	subss XMM1, XMM1 ; Zera registrador
	comiss XMM0, XMM1 ; Verifica sinal
	jae Rot7 ; Salta se número é positivo
	mov DL, '-' ; Senão, escreve sinal -
	mov [RSI], DL ; Carrega na memória o sinal -
	mov RDX, -1 ; Carrega -1 em RDX
	cvtsi2ss XMM1, RDX ; Converte -1 para real
	mulss XMM0, XMM1 ; Tranforma o valor em positivo
	add RSI, 1 ; Incrementa o ponteiro

Rot7:
	roundss XMM1, XMM0, 0b0011 ; Parte inteira de XMM1
	subss XMM0, XMM1 ; Parte fracionária de XMM0
	cvtss2si RAX, XMM1 ; Convertido para int
; Converte parte inteira que está em RAX
Rot8:
	add RCX, 1 ; Incrementa contador
	cdq ; Estende edx:eax para divisão
	idiv EBX ; Divide edx;eax por ebx
	push DX ; Empilha valor do resto
	cmp EAX, 0 ; Compara se quociente é 0
	jne Rot8 ; Se não é 0, continua
	sub RDI, RCX ; Decrementa precisão

; Agora, desempilha valores e escreve parte inteira
Rot9:
	pop DX ; Desempilha valor
	add DL, '0' ; Transforma em caractere
	mov [RSI], DL ; Escreve caractere
	add RSI, 1 ; Incrementa base
	sub RCX, 1 ; Decrementa contador
	cmp RCX, 0 ; Verifica pilha vazia
	jne Rot9 ; Se não estiver vazia, loop
	mov DL, '.' ; Escreve ponto decimal
	mov [RSI], DL ; Armazena ponto
	add RSI, 1 ; Incrementa base

; Converte para fracionária que está em XMM0
Rot10:
	cmp RDI, 0 ; Verifica precisão
	jle Rot11 ; Terminou precisão ?
	mulss XMM0, XMM2 ; Desloca para esquerda
	roundss XMM1, XMM0, 0b0011 ; Parte inteira XMM1
	subss XMM0, XMM1 ; Atualiza XMM0
	cvtss2si RDX, XMM1 ; Convertido para int
	add DL, '0' ; Transforma em caractere
	mov [RSI], DL ; Escreve caractere
	add RSI, 1 ; Incrementa ponteiro
	sub RDI, 1 ; Decrementa precisão
	jmp Rot10; Loop

; Impressão
Rot11:
	mov DL, 0 ; Fim string
	mov [RSI], DL ; Escreve caractere
	mov RDX, RSI ; Calcula tamanho da string convertido
	mov RBX, M+0x0 ; Salva endereço do buffer
	sub RDX, RBX ; Tam = RSI - M - Buffer.end
	mov RSI, M+0x0 ; Endereço do buffer

; Escreve a string para a saída padrão
	mov RAX, 1 ; Chamada de escrita
	mov RDI, 1 ; Escrever para a saída padrão
	syscall ; Chama o kernel
section .data
	db ": Ola' " ; Declaração da string
	db 0 ; Declaração do fim da string
section .text
; Impressão String
; Calcula o tamanho da string
	mov RSI, M+0x1011e ; Move o endereço da string para o registrador fonte (source)
	mov RDX, RSI ; Recupera o endereço inicial da string
Rot12: ; Loop para o calculo do tamanho
	mov AL, [RDX] ; Le o caractere atual
	add RDX, 1 ; Incrementa o ponteiro da string
	cmp AL, 0 ; Verifica se o caractere lido eh o byte 0 (fim de string)
	jne Rot12 ; Caso nao seja, continua o loop

	sub RDX, RSI ; Subtrai o endereco inicial da string pelo endereço final, o registrador RDX sera utilizado pela chamada de sistema como o tamanho da string
	sub RDX, 1 ; Desconsidera o byte nulo ao final da string

; Escreve a string para a saída padrão
	mov RSI, M+0x1011e ; Move o endereço da string para o registrador fonte (source)
	mov RAX, 1 ; Chamada de escrita
	mov RDI, 1 ; Escrever para a saida padrao
	syscall ; Chama o kernel
; Impressão String
; Calcula o tamanho da string
	mov RSI, M+0x10004 ; Move o endereço da string para o registrador fonte (source)
	mov RDX, RSI ; Recupera o endereço inicial da string
Rot13: ; Loop para o calculo do tamanho
	mov AL, [RDX] ; Le o caractere atual
	add RDX, 1 ; Incrementa o ponteiro da string
	cmp AL, 0 ; Verifica se o caractere lido eh o byte 0 (fim de string)
	jne Rot13 ; Caso nao seja, continua o loop

	sub RDX, RSI ; Subtrai o endereco inicial da string pelo endereço final, o registrador RDX sera utilizado pela chamada de sistema como o tamanho da string
	sub RDX, 1 ; Desconsidera o byte nulo ao final da string

; Escreve a string para a saída padrão
	mov RSI, M+0x10004 ; Move o endereço da string para o registrador fonte (source)
	mov RAX, 1 ; Chamada de escrita
	mov RDI, 1 ; Escrever para a saida padrao
	syscall ; Chama o kernel
; Da a quebra de linha
	mov RSI, M+0x0
	mov [RSI], byte 10
	mov RDX,1 ; ou buffer.tam
	mov RAX, 1 ; chamada para saída
	mov RDI, 1 ; saída para tela
	syscall


; Inicio atribuicao: n
	mov EBX, 1 ; Coloca o inteiro no EBX
	mov [M+0x5], EBX ; Carrega para a memória o inteiro
; Soma ou subtração de termos
	mov EAX, [M+0x10000] ; Moveu a T1(int) para EAX
	mov EBX, [M+0x5] ; Moveu a T2(int) para EBX
	add EAX, EBX ; Realiza a soma do conteudo de EAX com EBX
	mov [M+0x1], EAX ; Coloca no temporario resultado da soma ou subtração
; Atribuicao int
	mov EAX, [M+0x1]
	mov [M+0x10000], EAX
	jmp Rot3
; Fim While
Rot4:
; Halt
mov rax, 60 ; Chamada de saída
mov rdi, 0 ; Código de saida sem erros
syscall ; Chama o kernel
