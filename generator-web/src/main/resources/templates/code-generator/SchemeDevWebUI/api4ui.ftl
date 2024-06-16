import Api from '@/api/API'

const api = new Api('masterdata', '${classInfo.className?uncap_first}')
api.del${classInfo.className} = Api.get('del${classInfo.className}', 'json')
api.get${classInfo.className} = Api.get('get${classInfo.className}')

export default api
