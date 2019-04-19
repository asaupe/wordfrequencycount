import axios from 'axios'

export const axiosGet = (path, params, includeToken) => {
  var header = { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }

  if (includeToken) {
    header = {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    }
  }

  return axios.get(path, header)
}

export const axiosPost = (path, params, includeToken) => {
  var header = { headers: { 'Content-Type': 'application/json' } }

  if (includeToken) {
    header = {
      headers: {
        'Content-Type': 'application/json'
      }
    }
  }

  return axios.post(path, params, header)
}

export default axiosPost
