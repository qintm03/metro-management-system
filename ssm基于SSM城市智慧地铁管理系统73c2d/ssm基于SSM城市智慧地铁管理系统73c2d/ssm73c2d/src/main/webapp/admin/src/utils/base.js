const base = {
    get() {
        return {
            url : "http://localhost:8080/ssm73c2d/",
            name: "ssm73c2d",
            // 退出到首页链接
            indexUrl: 'http://localhost:8080/ssm73c2d/front/dist/index.html'
        };
    },
    getProjectName(){
        return {
            projectName: "基于SSM城市智慧地铁管理系统"
        } 
    }
}
export default base
