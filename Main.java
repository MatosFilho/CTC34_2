import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;


public class Main {
	static List<Aresta> A;
	static int proxEstado;

	public static void identificaAutomato(String exp) {
		A = new ArrayList<Aresta>();
		A.add(new Aresta(0,1,exp));
		proxEstado=2;
		for(int j=0;j<3;j++){	
			for(int i=0;i<A.size(); i++){
				verificaUniao(i);
			}
			for(int i=0;i<A.size(); i++){
				verificaConcatenacao(i);
			}
			for(int i=0;i<A.size(); i++){
				verificaFechoDeKleene(i);
			}
			for(int i=0;i<A.size(); i++){
				removeParenteses(i);
			}
		}
	}
	
	private static void verificaFechoDeKleene(int i) {
		System.out.println(A.get(i).expressao);
		int posicao = A.get(i).expressao.indexOf('*');
		if(posicao==-1) return;
		else if(posicao ==  A.get(i).expressao.length()-1){
			int j = A.get(i).destino;
			String e = "";
			for(int k=0; k<A.get(i).expressao.length()-1;k++)
				e+=A.get(i).expressao.charAt(k);
			A.set(i, new Aresta(A.get(i).origem,proxEstado,"&"));
			A.add(new Aresta(proxEstado,proxEstado,e));
			A.add(new Aresta(proxEstado,j,"&"));
			proxEstado++;
		}
	}

	private static void removeParenteses(int posicao) {
		String e=A.get(posicao).expressao;
		if(e.length()==1) return;
		if(e.charAt(0)!='(' && e.charAt(e.length()-1)!=')') return;
		else if(e.charAt(e.length()-1)!=')'){
			e="";
			for(int i=1;i<A.get(posicao).expressao.length();i++){
				e+=A.get(posicao).expressao.charAt(i);
			}
		}
		else if(e.charAt(0)!='('){
			e="";
			for(int i=0;i<A.get(posicao).expressao.length()-1;i++){
				e+=A.get(posicao).expressao.charAt(i);
			}
		}
		else{
			e="";
			for(int i=1;i<A.get(posicao).expressao.length()-1;i++){
				e+=A.get(posicao).expressao.charAt(i);
			}
		}
		A.set(posicao, new Aresta(A.get(posicao).origem,A.get(posicao).destino,e));
	}

	private static void verificaConcatenacao(int posicao) {
		String e=A.get(posicao).expressao, depois="", antes="";
		int estadoFinal, numParenteses=0;
		if(e.length()==1) return;
		if(e.charAt(0)=='('){
			for(int j=0;j<e.length();j++){
				if(e.charAt(j)=='(') numParenteses++;
				if(e.charAt(j)==')') numParenteses--;
				antes+=e.charAt(j);
				if(numParenteses==0 && j!= e.length()-1 && e.charAt(j+1)!='*'){
					for(int k=j+1; k<e.length();k++){
						depois+=e.charAt(k);
					}
					estadoFinal=A.get(posicao).destino;
					A.set(posicao, new Aresta(A.get(posicao).origem,proxEstado,antes));
					A.add(new Aresta(proxEstado++,estadoFinal,depois));
					j=e.length();
				}
				else if(numParenteses==0 && j== e.length()-1) return;
			}
		}
		else{
			for(int j=0;j<e.length();j++){
				antes+=e.charAt(j);
				if(j+1!= e.length() && e.charAt(j+1)=='*'){
					antes+=e.charAt(j+1);
					j++;
				}
				if( j+1!= e.length() && (e.charAt(j+1)!='+' || e.charAt(j+1)!='(' || e.charAt(j+1)!=')') ){
					for(int k=j+1; k<e.length();k++){
						depois+=e.charAt(k);
					}
					estadoFinal=A.get(posicao).destino;
					A.set(posicao, new Aresta(A.get(posicao).origem,proxEstado,antes));
					A.add(new Aresta(proxEstado++,estadoFinal,depois));
					j=e.length();
				}
			}
		}
	}

	private static void verificaUniao(int posicao) {
		String e,antes="",depois="";
		int numParenteses=0;
		e=A.get(posicao).expressao;
		if(e.length()==1) return;
		for(int j=0;j<e.length();j++){
			if(e.charAt(j)!='+')
				antes+=""+e.charAt(j);
			if(e.charAt(j)!='(')
				numParenteses++;
			if(e.charAt(j)!=')')
				numParenteses--;
			if(e.charAt(j)=='+' && numParenteses==0){
				for(int k=j+1; k<e.length();k++){
					depois+=e.charAt(k);
				}
				A.set(posicao, new Aresta(A.get(posicao).origem,A.get(posicao).destino,antes));
				A.add(new Aresta(A.get(posicao).origem,A.get(posicao).destino,depois));
				j=e.length();
			}
		}
	}
	
	public static int numeroDeArestas(){
		return A.size();
	}
	
public String retornaEstados()throws IOException {
		
		//para imprimir no arquivo
		Formatter arquivo;
		arquivo = new Formatter("Digraph.txt");
		arquivo.format("digraph G {");
		arquivo.format("\r\n\r\trankdir=LR;");
		arquivo.format("\r\n\r\tnode [shape = doublecircle]; 0 1;");
		arquivo.format("\r\n\r\tnode [shape = circle];");
		//atÃ© aqui o cabeÃ§alho
		
		String result="";
		int ultimoVertice = A.get(0).destino;
		result+="0->"+A.get(0).expressao+"->"+ultimoVertice;
		
		//ImpressÃ£o primeira aresta
		arquivo.format("\r\n\r\t0 -> %s [ label = \"%s\" ]", ultimoVertice, A.get(0).expressao);
		
		for(int i=1; i<A.size(); i++){
			if(A.get(i).origem==ultimoVertice){
				result+="->"+A.get(i).expressao+"->"+A.get(i).destino;
				
				//Imprime aresta concatenaÃ§Ã£o
				arquivo.format("\r\n\r\t%s -> %s [ label = \"%s\" ]", ultimoVertice, A.get(i).destino, A.get(i).expressao);
				
				ultimoVertice = A.get(i).destino;
			} else{
				result+=";"+A.get(i).origem+"->"+A.get(i).expressao+"->"+A.get(i).destino;
				
				//Imprime demais arestas
				arquivo.format("\r\n\r\t%s -> %s [ label = \"%s\" ]", A.get(i).origem, A.get(i).destino, A.get(i).expressao);
				
				ultimoVertice = A.get(i).destino;
			}
		}
		arquivo.format("\r\n}");
		arquivo.close();
		return result;
	}

	public boolean verificaSentenca(int estadoInicial, String sentenca) {
		boolean result = false;
		System.out.println(sentenca);
		if(estadoInicial!=1){
			if(sentenca.length()==0){
				for(int i=0; i<A.size(); i++){
					if(A.get(i).origem==estadoInicial && A.get(i).destino==1 && A.get(i).expressao.equals("&"))
						result = true;
				}
				return result;
			}else{
				for(int i=0; i<A.size(); i++){
					if(A.get(i).origem==estadoInicial && (A.get(i).expressao.contains(""+sentenca.charAt(0)) || A.get(i).expressao.contains("&")))
						result = verificaSentenca(A.get(i).destino, sentenca.substring(1));
					if (result) return true;
				}
				return false;
			}
		}else{
			if(sentenca.length()==0) return true;
			else return false;
		}
	}
	public static void main(String[] args) throws IOException {
		Main M = new Main();
		M.identificaAutomato("a*b*c*");
		M.retornaEstados();
	}
}
