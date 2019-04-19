import React, { Component } from 'react';
import axios from 'axios'

import {
    Accordion,
    AccordionItem,
    AccordionItemHeading,
    AccordionItemButton,
    AccordionItemPanel,
} from 'react-accessible-accordion';
import Checkbox from 'rc-checkbox';
import 'rc-checkbox/assets/index.css';

import WordCountTable from './WordCountTable'

// Demo styles, see 'Styles' section below for some notes on use.
import 'react-accessible-accordion/dist/fancy-example.css';

class FileAccordian extends Component {
    constructor(props) {
        super(props)
    
        this.state = {
          files: [],
          stemWords: true,
          stopWords: true,
          selectedFile: null,
          fileStatus: ''
        }
        this.onChangeStopWords = this.onChangeStopWords.bind(this)
        this.onChangeStemWord = this.onChangeStemWord.bind(this)
        this.loadWordCounts = this.loadWordCounts.bind(this)
    }

    componentDidMount() {
        this.loadWordCounts(this.state.stopWords, this.state.stemWords)
    }

    loadWordCounts() {
        var thisForm = this
        let apiCall = 'http://localhost:8080/wordcount?name=SampleTextFile_1000kb&includeStopWords='
        apiCall += this.state.stopWords
        apiCall += '&includeStemWords=' + this.state.stemWords

        axios.get(apiCall, null)
          .then(function(response) {
            thisForm.setState({ files: response.data.files })
        })
          .catch(function(error) {
            console.log(error)
        })
    }

    onChangeStemWord(e) {
        this.setState({ stemWords: e.target.checked });
    }

    onChangeStopWords(e) {
        this.setState({ stopWords: e.target.checked });
    }

    onChangeHandler=event=>{
        this.setState({
            selectedFile: event.target.files[0],
            loaded: 0,
            fileStatus: ''
        })
    }

    onClickHandler = () => {
        const data = new FormData() 
        data.append('file', this.state.selectedFile)
        axios.post("http://localhost:8080/file/upload", data, { // receive two parameter endpoint url ,form data 
        })
        .then(res => { // then print response status
            this.setState({ fileStatus: 'Your file was successfully uploaded for processing.'})
            console.log(res.statusText)

        })
    }

    render() {
        return (
            <div>
                <h3>Upload File</h3>
                <div align='left'>
                    <input type="file" name="file" onChange={this.onChangeHandler}/><br />
                    <button type="button" class="btn btn-success btn-block" onClick={this.onClickHandler}>Upload</button><br />
                    {this.state.fileStatus}
                </div>
                <h3>Review Word Counts</h3>
                <div align="left">
                    <p>
                        <label>
                        <Checkbox
                            name="stopWords"
                            defaultChecked
                            onChange={this.onChangeStopWords}
                        />
                        Include Stop Words
                        </label>
                        &nbsp;&nbsp;
                    </p>
                    <p>
                        <label>
                        <Checkbox
                            name="stemWords"
                            defaultChecked
                            onChange={this.onChangeStemWord}
                        />
                        Include Stem Words
                        </label>
                        &nbsp;&nbsp;
                    </p>
                    <button onClick={this.loadWordCounts}>Reload Word Counts</button>
                </div>
                <Accordion allowMultipleExpanded='true'>
                    {this.state.files.map((item, i) =>
                        <AccordionItem key={i}>
                            <AccordionItemHeading>
                                <AccordionItemButton>
                                    {item.fileName}
                                </AccordionItemButton>
                            </AccordionItemHeading>
                            <AccordionItemPanel>
                                <div align='left'><a target='_blank' href={'http://localhost:8080/file?file=' + item.fileName}>View File</a></div>
                                <WordCountTable wordCounts={item.wordCounts} stemWord={this.state.stemWords}/>
                            </AccordionItemPanel>
                        </AccordionItem>
                    )}
                </Accordion>
            </div>
        )
    }
}

export default FileAccordian