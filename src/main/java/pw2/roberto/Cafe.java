package pw2.roberto;

public class Cafe {

	public Integer id;
	public String nome;
	public String paisOrigem;
	public Integer preco;

	public Cafe() {
	}

	public Cafe(Integer id, String nome, String paisOrigem, Integer preco) {
		this.id = id;
		this.nome = nome;
		this.paisOrigem = paisOrigem;
		this.preco = preco;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getPaisOrigem() {
		return paisOrigem;
	}

	public void setPaisOrigem(String paisOrigem) {
		this.paisOrigem = paisOrigem;
	}

	public Integer getPreco() {
		return preco;
	}

	public void setPreco(Integer preco) {
		this.preco = preco;
	}

}
