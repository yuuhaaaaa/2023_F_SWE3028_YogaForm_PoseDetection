package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.demo.dto.pose.Joints;
import com.example.demo.dto.pose.Landmark;
import com.example.demo.dto.pose.Pose;
import com.example.demo.repository.PoseRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PoseService {

    private final PoseRepository poseRepository;

    public Pose getPosebyName(String name){

        Pose pose = poseRepository.findByName(name);
        return pose;   
    }

    public void saveAll(List<Pose> poses){
        for (Pose pose: poses){
            poseRepository.save(pose);
        }
    }

    public List<Landmark> landmarkArray = new ArrayList<>();

    public Double calculateEach(Integer one, Integer two, Integer three){
        Landmark zero = new Landmark(0.0, 0.0, 0.0, 0.0);
        Landmark a =  (landmarkArray.get(one)!=null) ? landmarkArray.get(one) : zero;
        Landmark b =  (landmarkArray.get(two)!=null) ? landmarkArray.get(two) : zero;
        Landmark c =  (landmarkArray.get(three)!=null) ? landmarkArray.get(three) : zero;

        // Landmark a = landmarkArray.get(one);          
        // Landmark b = landmarkArray.get(two);
        // Landmark c = landmarkArray.get(three);

        // if(a==null){a=zero;}
        // if(b==null){b=zero;}
        // if(c==null){c=zero;}

        // System.out.println("one: "+ landmarkArray.get(one).toString());
        // System.out.println("b: "+b.toString());
        // System.out.println(c);


        // Calculate the dot product and the magnitudes of the vectors
        Double dot_product =  ((b.getX() - a.getX())*(b.getX() - c.getX()) + (b.getY() - a.getY())*(b.getY() - c.getY()));
        Double point_1_2_mag =  (Math.sqrt(Math.pow((b.getX() - a.getX()),2) + Math.pow((b.getY() - a.getY()),2)));
        Double point_2_3_mag =  (Math.sqrt(Math.pow((b.getX() - c.getX()),2) + Math.pow((b.getY() - c.getY()),2)));

        // Calculate the angle between the left hand, elbow, and shoulder landmarks in degrees
        Double aangle = (Math.acos(dot_product / (point_1_2_mag * point_2_3_mag))* (180 / Math.PI));
        System.out.println(Double.toString(aangle));

        return aangle;

    }
    public Joints calculateAll(List<Landmark> landmarkArrays){

        landmarkArray = landmarkArrays;

        //왼 팔꿈치 11 13 15
        Double lelbow = calculateEach(11, 13, 15);

        //오른팔꿈치 12 14 16
        Double relbow = calculateEach(12, 14, 16);

        //왼어깨 12 11 13
        Double lshoulder = calculateEach(12, 11, 13);

        //오른어깨 11 12 14
        Double rshoulder = calculateEach(11, 12, 14);


        //왼무릎 23 25 27
        Double lknee = calculateEach(23, 25, 27);


        //오른무릎 24 26 28
        Double rknee = calculateEach(24, 26, 28);


        // //왼골반 24 23 25
        // Double lhip = calculateEach(24, 23, 25);


        // //오른골반 23 24 26
        // Double rhip = calculateEach(23, 24, 26);


        Joints joints = new Joints("", lelbow, relbow, lshoulder, rshoulder, lknee, rknee);

        return joints;
    }

    private List<String> tooStraight = new ArrayList<>();
    private List<String> tooCurved = new ArrayList<>();


    public void compareEach(String name, Double pose, Double joint){
            Double minval = pose*0.95;
            Double maxval = pose*1.05;
            if (maxval < joint){
                tooStraight.add(name);
            }
            else if(minval > joint){
                tooCurved.add(name);
            }
            return ;
    }

    public String comparePose(Pose pose, Joints joints){

        compareEach("left elbow", pose.getLelbow(), joints.getLelbow());
        compareEach("right elbow", pose.getRelbow(), joints.getRelbow());
        compareEach("left shoulder", pose.getLshoulder(), joints.getLshoulder());
        compareEach("right shoulder", pose.getRshoulder(), joints.getRshoulder());
        compareEach("left knee", pose.getLknee(), joints.getLknee());
        compareEach("right knee", pose.getRknee(), joints.getRknee());

        // boolean curved = tooCurved.isEmpty();
        // boolean straight = tooStraight.isEmpty();
        String feedback = "";
        if(tooCurved.isEmpty()==false){
            feedback="Straighten ";
            for(String name : tooCurved){
                feedback=feedback+name+", ";
            }
            feedback+=". ";
        }
        if(tooStraight.isEmpty()==false){
            feedback+="Curl ";
            for(String name : tooStraight){
                feedback=feedback+name+", ";
            }
            feedback+=". ";
        }
        if(feedback==""){
            feedback="Good job.";
        }
        return feedback;

    }

    private Integer stop=0;

    public void compareAngle(String name, Double joint, Double newjoint){
        Double minval = joint*0.95;
        Double maxval = joint*1.05;
        if ((maxval < joint) || (minval > joint)){
            return;
        }
        stop+=1;
        return ;
    }

    public boolean compareAngles(Joints currentJoints, Joints newJoints) {
        stop=0;

        compareAngle("left elbow", currentJoints.getLelbow(), newJoints.getLelbow());
        compareAngle("right elbow", currentJoints.getRelbow(), newJoints.getRelbow());
        compareAngle("left shoulder", currentJoints.getLshoulder(), newJoints.getLshoulder());
        compareAngle("right shoulder", currentJoints.getRshoulder(), newJoints.getRshoulder());
        compareAngle("left knee", currentJoints.getLknee(), newJoints.getLknee());
        compareAngle("right knee", currentJoints.getRknee(), newJoints.getRknee());

        if(stop>3) return true;
        return false;
    }
}