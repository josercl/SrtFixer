package com.github.josercl.logic;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SrtFixer {
	//
	public final static String timeRegEx="(\\-)?(\\d{0,2}:\\d{0,2}:\\d{0,2}(,\\d{0,3})?)";
	String regEx=timeRegEx+"\\s*\\-\\->\\s*"+timeRegEx;
	String regExLineNumber="^\\d+$";
	Pattern timePattern=Pattern.compile(regEx);
	String fileName;
	
	public SrtFixer(){}
	
	public void fixFile(String file,String timeDiff,int lineDiff){
		int millis=convertToMillis(timeDiff);
		try {
                    FileReader fr=new FileReader(file);
                    BufferedReader br=new BufferedReader(fr);
                    String line,lineutf8,output="";
                    while((line=br.readLine())!=null){
                        if(Pattern.matches(regExLineNumber, line)){
                            output+=(Integer.parseInt(line)+lineDiff)+"\n";
                        }else if(Pattern.matches(regEx, line)){
                            output+=(fixTime(line, millis))+"\n";
                        }else{
                            lineutf8=new String(line.getBytes(),"UTF-8");
                            output+=(lineutf8)+"\n";
                        }
                    }
                    br.close();
                    
                    /*int extIndex=file.toLowerCase().lastIndexOf(".srt");
                    String fixedFile=file.substring(0, extIndex)+".FIXED.srt";*/
                    
                    FileWriter fw=new FileWriter(file);
                    fw.write(output);
                    fw.close();
                    
		} catch (FileNotFoundException e) {
                    e.printStackTrace();
		} catch (IOException ioe) {
                    ioe.printStackTrace();
		}
	}
	
	private int convertToMillis(String timeDiff){
		String []pieces=timeDiff.split(":");
                int h=Integer.parseInt(pieces[0]);
		int m=Integer.parseInt(pieces[1]);
		String []pieces2=pieces[2].split(",");
		int s=Integer.parseInt(pieces2[0]);
		int millis=0;
		if(pieces2.length>1){
			millis=Integer.parseInt(pieces2[1]);
		}
		
		if(timeDiff.charAt(0)=='-' || h<0){
			m*=-1;
			s*=-1;
			millis*=-1;
		}
		
		return millis + s*1000 + m*60000 + h*3600000;
	}
	
	private String convertFromMillis(int millis){
		String format="%02d";
		String formatMillis="%03d";
		int h=millis/3600000;
		millis%=3600000;
		int m=millis/60000;
		millis%=60000;
		int s=millis/1000;
		millis%=1000;
		return String.format(format,h)+":"+String.format(format,m)+":"+String.format(format,s)+","+String.format(formatMillis,millis);
	}
	
	private String fixTime(String time, int timeDiff){
		Matcher match=timePattern.matcher(time);
		match.matches();
		int start= convertToMillis(match.group(2))+timeDiff;
		int end  = convertToMillis(match.group(5))+timeDiff;
		
		return convertFromMillis(start)+" --> "+convertFromMillis(end);
	}
	
	public void showHelp(){
		System.out.println("Usage: java SrtFixer file time_adjusment [line_number_adjustment]");
		System.out.println("file\t\t\tThe path of the file to fix");
		System.out.println("time_adjustment\t\tTime difference to fix srt times, must be specified in format [-]hh:mm:ss,SSS");
		System.out.println("line_number_adjustment\t(Optional) Adjustment to use for subtitles number");
	}
	
	/*public static void main(String[] args){
		//file timeDiff [lineDiff]
		if(args.length<2 || !Pattern.matches(timeRegEx, args[1])){
			new SrtFixer().showHelp();
		}else{
			int lineDiff=0;
			if(args.length>2){
				lineDiff=Integer.parseInt(args[2]);
			}
			new SrtFixer().fixFile(args[0],args[1],lineDiff);
		}
	}*/
}
