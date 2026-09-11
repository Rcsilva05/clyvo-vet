# Diagramas — Projeto SOLIN

## Como visualizar

Os diagramas estão em **Mermaid** dentro de arquivos Markdown. O GitHub renderiza Mermaid automaticamente — basta abrir os arquivos `.md` direto no navegador no repositório que eles aparecem como imagens.

| Arquivo | O que é |
|---|---|
| [`diagrama-de-classes.md`](./diagrama-de-classes.md) | Diagrama de Classes das entidades JPA |
| [`DER.md`](./DER.md) | Diagrama Entidade-Relacionamento (modelo físico do banco) |

## Para gerar imagens PNG (opcional)

Se precisar das versões em imagem para apresentação:

1. Acesse https://mermaid.live
2. Cole o conteúdo dentro do bloco \`\`\`mermaid de cada arquivo
3. Use os botões de download (PNG/SVG) no canto superior direito

Ou via linha de comando, com `@mermaid-js/mermaid-cli`:
```bash
npm install -g @mermaid-js/mermaid-cli
mmdc -i diagrama-de-classes.md -o diagrama-de-classes.png
mmdc -i DER.md -o DER.png
```
