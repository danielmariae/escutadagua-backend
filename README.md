## EscutaD'Água - Backend (Quarkus)

API para monitoramento de consumo de água, registro de eventos, emissão de alertas e gestão de usuários. Construída com Quarkus 3, Hibernate ORM/Panache, PostgreSQL e JWT.

URL Swagger UI (dev): http://localhost:8080/q/swagger-ui
Dev UI (dev): http://localhost:8080/q/dev

Observação: este projeto nasceu no contexto de um hackathon e mapeia funcionalidades típicas de soluções de economia de água: metas pessoais, eventos detectados (ex.: som de descarga, torneira aberta), alertas inteligentes e histórico de consumo.

## Sumário

- Entidades do domínio e atributos
- DTOs e contratos
- Endpoints da API
- Autenticação e autorização
- Estrutura do projeto
- Como executar (Windows PowerShell)
- Banco de dados (Docker/PostgreSQL)
- Empacotamento e execução (JAR/Uber/Nativo)
- Alinhamento com as ideias do Hackathon 2025

## Entidades do domínio e atributos

Todas as entidades usam JPA com Panache. Tipos entre parênteses são Java/BD. Campos obrigatórios estão marcados.

1) Usuario
- id (Long)
- nomeCompleto (String) [obrigatório]
- email (String) [obrigatório]
- senha (String) [obrigatório; armazenada com hash]
- metaDiaria (Double) — minutos de água por dia (meta pessoal)

2) EventoAgua
- id (Long)
- usuario (Usuario) [obrigatório]
- dataHoraEvento (LocalDateTime) [obrigatório]
- tipoEvento (Enum TipoEvento) [obrigatório]
	- Valores: CHUVEIRO, GOTEIRA, SOM_DESCARGA, TORNEIRA_ABERTA
- gastoAgua (Double) [obrigatório] — litros
- duracaoEvento (Double) [obrigatório] — minutos

3) ConsumoAgua
- id (Long)
- usuario (Usuario) [obrigatório]
- diaConsumo (LocalDateTime) [obrigatório] — referência do dia
- totalConsumo (Double) [obrigatório] — litros no dia
- eventosDia (List<EventoAgua>) — eventos associados ao dia

4) Alerta
- id (Long)
- usuario (Usuario) [obrigatório]
- tipoAlerta (Enum TipoAlerta)
	- Valores: ALTO_CONSUMO, BANHO_LONGO, USO_DE_AGUA_NOTURNO, VAZAMENTO_DETECTADO
- titulo (String) [obrigatório]
- mensagem (String) [obrigatório]
- notificadoEm (LocalDateTime)
- lido (Boolean)

## DTOs e contratos

Autenticação e Usuário
- LoginDTO: { email:String, senha:String }
- UsuarioDTO: { nomeCompleto:String, email:String, senha:String, metaDiaria:Double }
- UsuarioResponseDTO: { id:Long, nomeCompleto:String, email:String, metaDiaria:Double }

Eventos de Água
- EventoAguaDTO: { idUsuario:Long, dataHoraEvento:ISO-8601, tipoEvento:String, gastoAgua:Double, duracaoEvento:Double }
- EventoAguaResponseDTO: { id:Long, idUsuario:Long, dataHoraEvento:ISO-8601, tipoEvento:String, gastoAgua:Double, duracaoEvento:Double }

Consumo de Água
- ConsumoAguaResponseDTO: { id:Long, idUsuario:Long, diaConsumo:ISO-8601, totalConsumo:Double, eventosDia:[EventoAguaResponseDTO] }

Alertas
- AlertaDTO: { idUsuario:Long, tipoAlerta:String, titulo:String, mensagem:String, notificadoEm:ISO-8601, lido:Boolean }
- AlertaResponseDTO: { id:Long, idUsuario:Long, tipoAlerta:String, titulo:String, mensagem:String, notificadoEm:ISO-8601, lido:Boolean }

## Endpoints da API

Autenticação/Usuários — /usuarios
- POST /usuarios/auth — autenticar
	- body: LoginDTO
	- 200: UsuarioResponseDTO + cabeçalho Authorization: "Bearer <token>"
	- 401: não autorizado
- POST /usuarios — criar usuário
	- body: UsuarioDTO
	- 201: UsuarioResponseDTO
- GET /usuarios — listar todos
- GET /usuarios/{id} — buscar por id
- GET /usuarios/email/{email} — buscar por email
- PUT /usuarios/{id} — atualizar
	- body: UsuarioDTO
- DELETE /usuarios/{id} — deletar

Eventos — /eventosagua (requer role User)
- POST /eventosagua — registrar evento
	- body: EventoAguaDTO
	- 201: EventoAguaResponseDTO
- GET /eventosagua/usuario/{idUsuario} — histórico por usuário

Consumo — /consumo (requer role User)
- GET /consumo/{idUsuario}/hoje — consumo agregado do dia
- GET /consumo/{idUsuario}/historico?dias=N — histórico dos últimos N dias (padrão: 7)

Alertas — /alertas (requer role User)
- GET /alertas/usuario/{idUsuario} — listar alertas do usuário
- PUT /alertas/{id}/lido — marcar como lido

Observações
- Endpoints com @RolesAllowed("User") exigem JWT válido no cabeçalho Authorization: Bearer <token>.
- O login retorna o token JWT no cabeçalho Authorization da resposta. Guarde e reenvie nas próximas requisições.

## Autenticação e autorização (JWT)

- Assinatura/validação configuradas em `src/main/resources/token/` (chaves PEM) e `application.properties`.
- Issuer: banco-de-horas-jwt
- Papel esperado: User

Fluxo rápido
1) POST /usuarios/auth com {"email":"maria@gmail.com", "senha":"123456"}
2) Copiar o cabeçalho Authorization da resposta
3) Usar em chamadas protegidas: Authorization: Bearer <token>

Usuários seed (dev)
- maria@gmail.com / 123456
- joao@gmail.com / 123456
As senhas estão pré-hashadas no `import.sql`. O banco é recriado a cada start em dev (veja abaixo).

## Estrutura do projeto

```
src/main/java/local/escutadagua/
	dto/              # Contratos de entrada/saída (records)
	errors/           # Tratamento de exceções
	model/            # Entidades JPA + enums
	repository/       # Repositórios (Panache)
	resource/         # Endpoints REST (JAX-RS)
	service/          # Regras de negócio (usuarios, eventos, alertas, consumo)
src/main/resources/
	application.properties  # Config da app, DB, JWT, Hibernate
	import.sql              # Dados iniciais (dev)
	token/                  # Chaves JWT
Dockerfile.*              # Opções de build container (JVM/Native)
pom.xml                   # Dependências (Quarkus 3.24.1, Postgres, JWT)
```

Principais libs
- quarkus-rest, quarkus-rest-jackson (JAX-RS/JSON)
- quarkus-hibernate-orm(-panache)
- quarkus-jdbc-postgresql
- quarkus-smallrye-jwt (+ build)
- quarkus-smallrye-openapi (Swagger UI)

## Como executar (Windows PowerShell)

Pré-requisitos
- Java 21 (JDK)
- Docker (opcional, para subir Postgres rapidamente)

1) Subir PostgreSQL (opcional via Docker)

```
docker run --name escutadagua_db -e POSTGRES_USER=escutadagua_adm -e POSTGRES_PASSWORD=hackaton2025 -e POSTGRES_DB=escutadagua_db -p 5432:5432 -d postgres:16-alpine
```

As credenciais e URL usadas pela API estão em `application.properties`:
- URL: jdbc:postgresql://localhost:5432/escutadagua_db
- user: escutadagua_adm / password: hackaton2025
- Em dev: `quarkus.hibernate-orm.database.generation=drop-and-create` (recria o schema e aplica `import.sql`). Evite em produção.

2) Rodar em modo dev (live reload)

```
./mvnw.cmd quarkus:dev
```

URLs úteis
- Swagger UI: http://localhost:8080/q/swagger-ui
- Dev UI: http://localhost:8080/q/dev

3) Testar autenticação (exemplo)
- POST /usuarios/auth com {"email":"maria@gmail.com","senha":"123456"}
- Use o token Bearer retornado nas rotas com role "User".

## Empacotamento e execução

Empacotar JAR
```
./mvnw.cmd package
```
Executar JAR
```
java -jar target/quarkus-app/quarkus-run.jar
```

Uber-JAR
```
./mvnw.cmd package -Dquarkus.package.jar.type=uber-jar
java -jar target/*-runner.jar
```

Nativo (GraalVM ou build em container)
```
./mvnw.cmd package -Dnative
# ou
./mvnw.cmd package -Dnative -Dquarkus.native.container-build=true
```

## Exemplos de payloads

Login
```
POST /usuarios/auth
{
	"email": "maria@gmail.com",
	"senha": "123456"
}
```

Criar usuário
```
POST /usuarios
{
	"nomeCompleto": "Fulano da Silva",
	"email": "fulano@example.com",
	"senha": "minhasenha",
	"metaDiaria": 15.0
}
```

Registrar evento de água (User)
```
POST /eventosagua
Authorization: Bearer <token>
{
	"idUsuario": 1,
	"dataHoraEvento": "2025-10-23T07:45:00",
	"tipoEvento": "CHUVEIRO",
	"gastoAgua": 45.5,
	"duracaoEvento": 8.0
}
```

Buscar histórico de consumo (User)
```
GET /consumo/1/historico?dias=7
Authorization: Bearer <token>
```

## Alinhamento com as ideias do Hackathon 2025

Este backend materializa os seguintes pilares:
- Monitoramento e histórico: `ConsumoAgua` e endpoints de histórico fornecem visão diária e por período, base para dashboards e analytics.
- Detecção e categorização de eventos: `EventoAgua` e `TipoEvento` cobrem padrões como CHUVEIRO, GOTEIRA, SOM_DESCARGA e TORNEIRA_ABERTA, que podem vir de sensores/IoT ou do app.
- Alertas inteligentes: `Alerta` + `TipoAlerta` (ALTO_CONSUMO, BANHO_LONGO, USO_NOTURNO, VAZAMENTO_DETECTADO) entregam “nudges” para reduzir desperdício e agir rápido.
- Metas e mudança de comportamento: `Usuario.metaDiaria` apoia gamificação e metas pessoais de economia.
- Integração mobile: endpoints foram pensados para consumo por um app (ex.: Flutter), com contratos simples e token JWT.
- Segurança e privacidade: JWT, papéis e isolamento por usuário viabilizam um caminho compatível com boas práticas (LGPD em camadas superiores).

## Notas finais

- Em dev o schema do banco é recriado a cada start (drop-and-create). Em produção, configure para `validate`/`update` e remova `import.sql`.
- Os Dockerfiles incluem opções para build JVM e nativo.
- A OpenAPI é exposta automaticamente (SmallRye OpenAPI) e a UI está em `/q/swagger-ui`.

