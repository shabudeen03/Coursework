from flask import Flask, render_template, send_from_directory
from flask_wtf import FlaskForm
from wtforms import FileField, SubmitField
from werkzeug.utils import secure_filename
import os
from wtforms.validators import InputRequired

app = Flask(__name__)
app.config['SECRET_KEY'] = 'onceuponatimecomeon1112'
app.config['UPLOAD_FOLDER'] = 'static/files'

class UploadFileForm(FlaskForm):
    file = FileField("File", validators=[InputRequired()])
    submit = SubmitField("Upload File")

@app.route('/', methods=['GET', 'POST'])
@app.route('/home', methods=['GET', 'POST'])
def home():
    form = UploadFileForm()
    files = os.listdir(os.path.join(os.path.abspath(os.path.dirname(__file__)), app.config['UPLOAD_FOLDER']))
    if(form.validate_on_submit()):
        file = form.file.data # grab the file
        file.save(os.path.join(os.path.abspath(os.path.dirname(__file__)), app.config['UPLOAD_FOLDER'], secure_filename(file.filename))) #Save the file
        return 'File has been uploaded.'
    return render_template('index.html', form=form, files=files)

# Route to download or serve a specific file
@app.route('/download/<filename>')
def download_file(filename):
    return send_from_directory(app.config['UPLOAD_FOLDER'], filename)

if __name__ == '__main__':
    # app.run(port=9999, debug=True, host='0.0.0.0')
    app.run(port=9999, debug=True, host='192.168.1.20')