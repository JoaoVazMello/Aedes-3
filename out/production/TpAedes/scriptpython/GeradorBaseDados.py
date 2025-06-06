import pandas as pand

# Import do csv para dentro do codigo.
# Coloque dentro das () os path de onde esta sua base.
df = pand.read_csv("games_may2024_full.csv")

# Lista de colunas para manter
# appID / name / release_date / required_age / price / genres / short_description

# Caso queira colocar mais colunas basta
colunas_necessarias = ["AppID", "name", "release_date", "required_age", "price", "genres", "short_description"]

# Transforma o nome das colunas em uma lista para ser percorrida
nomes_colunas = df.columns.tolist()

# Limita uma coluna a um tamanho maximo de 250 caracteres
# Caso queira trocar basta mudar o nome de dos [] e a quantidade de caracteres
df["short_description"] = df["short_description"].astype(str).apply(lambda x: x[:250])

# For-each que percorre todo a lista de colunas necessarias testando se aquela coluna esta dentro da lista senao estiver ela e apagada
for coluna in df.columns:
  if coluna not in colunas_necessarias:
    df = df.drop(coluna, axis = 1)

# Remove ";" do meio da base de dados caso aconteça
df["name"] = df["name"].str.replace(";", "", regex=False)

# Salva a nova base de dados sem index
# Caso queira que ela crie um index altomatico basta trocar o index = False para index = True 
df.to_csv("BaseDeDados.csv", index = False, sep = ";", encoding = "utf-8")