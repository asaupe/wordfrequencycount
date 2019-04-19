import React, { Component } from 'react';
import PropTypes from 'prop-types'
import Table from 'rc-table';

class WordCountTable extends Component {
    constructor(props) {
        super(props)
    
        if (props.stemWord) {
            this.state = {
                columns:  [
                    {title: 'Word', dataIndex: 'word', key:'word', width: 100},
                    {title: 'Count', dataIndex: 'total', key:'total', width: 100}
                ]
            }
        } else {
            this.state = {
                columns:  [
                    {title: 'Word', dataIndex: 'word', key:'word', width: 100},
                    {title: 'Count', dataIndex: 'actualCount', key:'actualCount', width: 100}
                ]
            }
        }
    }
    
    componentWillReceiveProps(nextProps) {
        if (nextProps.stemWord) {
            this.setState({columns: [
                {title: 'Word', dataIndex: 'word', key:'word', width: 100},
                {title: 'Count', dataIndex: 'total', key:'total', width: 100}
            ]})
        } else {
            this.setState({columns: [
                {title: 'Word', dataIndex: 'word', key:'word', width: 100},
                {title: 'Count', dataIndex: 'actualCount', key:'actualCount', width: 100}
            ]}) 
        }
    }

    render() {
        return (
            <Table columns={this.state.columns} data={this.props.wordCounts} />
        )
    }
}

WordCountTable.propTypes = {
    wordCounts: PropTypes.array.isRequired,
    stemWord: PropTypes.bool.isRequired
}

export default WordCountTable