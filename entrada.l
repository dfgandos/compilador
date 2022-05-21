integer n=1;
string nome;
const MAXITER=12;

write("Digite seu nome: ");
readln(nome);

real percentual;
while n<=MAXITER begin
  percentual=real(n)/MAXITER;
  writeln(percentual,": Ola' ",nome);
  n=n+1;
end

