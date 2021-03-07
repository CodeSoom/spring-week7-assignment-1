# 인가(Authorization) 구현하기

## 과제 목표

지금은 로그인을 했다고 하더라도, 다른 사람의 정보를 내 마음대로 수정할 수 있습니다. 
내 정보는 오직 나만 수정할 수 있어야 합니다. 오직 나만이 내 정보를 수정할 수 있도록 
Spring Security를 이용해서 구현해 주세요.  

현재 사용자의 비밀번호를 평문으로 저장하고 있습니다. 누군가 외부에서 침투를 하거나 혹은 
내부에서 비밀번호를 탈취를 하더라도 원래 비밀번호를 알아낼 수 없도록 비밀번호를 암호화하여
저장해 주세요.

### 로그인이 필요없는 API

* 로그인 - `POST /session`
* 회원 생성하기 - `POST /users`
* 고양이 장난감 목록 얻기 - `GET /products`
* 고양이 장난감 상세 조회하기 - `GET /products/{id}`

### 로그인이 필요한 API

* 고양이 장난감 등록하기 - `POST /products`
* 고양이 장난감 수정하기 - `PATCH /products/{id}`
* 고양이 장난감 삭제하기 - `DELETE /products/{id}`
* 회원 수정하기 - `POST /users/{id}`
* 회원 삭제하기 - `DELETE /users/{id}`

## 요구 사항

- 테스트 커버리지 100%를 달성해야 합니다.
- 모든 API 테스트를 통과해야 합니다.
- 모든 E2E 테스트를 통과해야 합니다.

## API 실행하기

```bash
./gradlew run
```

## 웹 실행하기

### 설치

```bash
$ cd web
$ npm install
```

### 실행

```bash
$ npm start
```

브라우저 `http://localhost:9000`로 열면 실행된 것을 확인할 수 있습니다.

## 테스트

### Spring 테스트 실행

```bash
$ ./gradlew test
```

### 커버리지 확인하기

테스트를 실행하면 자동으로 커버리지 정보를 수집하여 저장합니다. 커버리지 정보는 `app/build/reports`
폴더에 저장됩니다. 커버리지 정보를 확인하려면 `app/build/reports/jacoco/test/html/index.html`파일을
브라우저에서 열면 확인할 수 있습니다.

### API 테스트 설치하기

```bash
$ cd tests
$ npm install
```

### API 테스트 실행

테스트는 실제로 동작하는 서버에 테스트하므로 서버가 동작하고 있는 상태여야 올바르게 동작합니다.

```bash
$ npm test
```

### E2E 테스트 실행하기

E2E테스트는 실제로 동작하는 서버와 실제로 동작하는 웹이 필요한 테스트하므로 서버가 동작하고, 웹 서버가 동작하고 있는 상태여야 올바르게 동작합니다.

```bash
$ cd web
$ npm run e2e
```
