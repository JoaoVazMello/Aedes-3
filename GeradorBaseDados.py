import pandas as pand

# import do csv para dentro do codigo
df = pand.read_csv("games_may2024_full.csv")

# lista de colunas para manter
# appID / name / release_date / required_age / price / genres

# caso queira colocar mais colunas basta

colunas_necessarias = ["AppID", "name", "release_date", "required_age", "price", "genres"]

# transforma o nome das colunas em uma lista para ser percorrida
nomes_colunas = df.columns.tolist()

for coluna in df.columns:
  if coluna not in colunas_necessarias:
    df = df.drop(coluna, axis = 1)

df.to_csv("BaseDeDados.csv", index = True, sep = ";", encoding = "utf-8")