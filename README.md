# MovieReservation

## 영화 예매 서비스

간단하게 영화를 예매할 수 있는 서비스입니다.

## Tech Stack
- Java (JDK 17)
- Spring Boot (3.3.2)
- MySQL
- Redis
- Elastic Search

## 프로젝트 기능 및 설계
- 회원가입/로그인/수정/탈퇴 기능
    - 관리자(owner)와 사용자(user)로 회원가입을 진행할 수 있다.
    - 회원가입 시 이메일(unique), 패스워드, 이름, 생년월일, 전화번호, 권한을 입력한다.
    - 회원가입 시 이메일 중복여부를 판단하여 예외 처리한다.
    - 가입 시 입력한 이메일과 패스워드를 이용해서 로그인이 가능하다.
    - 생년월일, 전화번호 등 개인정보를 수정할 수 있다.
    - 회원탈퇴를 하면 사용자의 정보를 삭제한다.
- 관리자
    - 영화관을 등록할 수 있다.
        - 영화관 이름(unique), 주소, 좌석수(100)
    - 영화를 등록할 수 있다.
        - 영화 제목(unique), 감독, 장르, 러닝타임, 개봉일
    - 영화 스케쥴을 등록할 수 있다.
        - 영화 id, 극장 id, 상영시각
        - 영화의 상영 기간에 대해서 관객 수 및 평점을 토대로 조절하는 것에 대해서 추후 개발 고려
- 사용자
    - 영화를 아래의 조건으로 검색할 수 있다.
        - 제목, 장르, 평점
        - 제목으로 검색할 때 일부 단어로 검색 가능하다. (ES)
    - 영화를 예매할 수 있다.
        - 개봉중인 영화 선택 -> 영화관 & 시간 선택 -> 좌석 선택
    - 예매한 영화를 취소할 수 있다.
    - 예매 내역을 확인할 수 있다.
        - 예매 내역, 취소 내역을 같이 확인할 수 있다.
    - 예매한 내역을 결제할 수 있다.
        - 결제 시스템은 mocking으로 예매 시 db에 입력되도록 구현
    - 영화 평점 및 리뷰 작성을 할 수 있다.
        - 예매한 영화의 상영시간이 지난 이후에 등록 가능하다.
        - 1-5점 사이의 평점과 한줄평을 남길 수 있다.

## ERD
![최최최종](https://github.com/user-attachments/assets/e06139d6-fbf5-4199-a07c-892d27e2ac65)




