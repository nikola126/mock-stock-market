**Mock Stock Market Project**
<br>**Nikola Yordanov**
---
Enable mocked **"buy"**, **"sell"** and **"quote"** operations with mocked money ("capital") with real data, provided by IEX Cloud.

Every visitor can request a **quote** for a given stock.

After registration, every user can buy or sell shares, provided he has enough capital and/or owns enough shares.

Every user can look at his portfolio, request real-time updates on stock value information (_by default an update is triggered every minute on stocks, which haven't been updated for longer than 15 minutes_), and look at the history of transactions of his account.

A basic set of account operations are allowed - a user can change his display name, password, add additional funds ("capital") to his account or register an IEX Cloud API Token, which will be used on any operations, requiring interaction with the API. (_Usually a default development API token is used for all users, which might lead to exhaustion of specific API call limit, provided enough users are using the application_).

---
**Deployment**

- Deployed on **Digital Ocean** Droplet at http://159.223.28.181/
- Frontend and Backend applications are containerized using **Docker**
- Default access (admin/admin) to **Grafana**


**Backend**

https://github.com/nikola126/mock-stock-market
- **Gradle** used as build system </li>
- **Checkstyle** and **PMD** used as code-quality plugins </li>
- Implemented using **Spring** and **Hibernate**
- Using **PostgreSQL** for database
- Unit tests using **JUnit** and **Mockito**
- Basic operation monitoring using **Prometheus** and **Grafana** 
- Tests and code-quality checks triggered on push using **GitHub Actions**
  
**Frontend**

https://github.com/nikola126/mock-stock-market-frontend

-  Implemented using **Javascript** and **React** </li>
-  Using **MUI** as UI library </li>


**Work in Progress**

  -  no authentication/encryption/security implemented so far, except basic username and password checks!
  -  user credentials are saved in local-storage, which enables basic auto-login and state-preservation after refresh
  -  no proxy setup, backend API is exposed
  -  no admin capabilities provided from the application itself
  -  no database backups
