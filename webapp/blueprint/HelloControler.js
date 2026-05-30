export default class HelloControler {

    constructor(section) {
        section.pageReady(() => {
            yojaWebApi.httpClient
                .get({ url: '/hello' })
                .then(res => {
                    section.firstTag('.message')
                           .textContent = res.body;
                });
        });
    }

}