# Imagem base oficial do Python
FROM python:3.11-slim

# Diretório de trabalho
WORKDIR /app

# Apenas um arquivo de teste (opcional)
RUN echo "Hello World!" > /app/test.txt

# Comando mínimo para o container
CMD ["cat", "/app/test.txt"]